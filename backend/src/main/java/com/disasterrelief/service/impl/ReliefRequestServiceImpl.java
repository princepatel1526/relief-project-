package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.ReliefRequestDto;
import com.disasterrelief.dto.response.ReliefRequestResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.ReliefRequest;
import com.disasterrelief.entity.StatusHistory;
import com.disasterrelief.entity.Victim;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.DisasterRepository;
import com.disasterrelief.repository.ReliefRequestRepository;
import com.disasterrelief.repository.StatusHistoryRepository;
import com.disasterrelief.repository.VictimRepository;
import com.disasterrelief.service.AuditLogService;
import com.disasterrelief.service.PriorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReliefRequestServiceImpl {

    private final ReliefRequestRepository reliefRequestRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final VictimRepository victimRepository;
    private final DisasterRepository disasterRepository;
    private final PriorityService priorityService;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<ReliefRequestResponse> getAllRequests(Long disasterId,
                                                       ReliefRequest.RequestStatus status,
                                                       Pageable pageable) {
        Pageable priorityPageable = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "priorityScore")
                    .and(Sort.by(Sort.Direction.DESC, "urgencyLevel"))
                    .and(Sort.by(Sort.Direction.ASC, "createdAt")));

        Page<ReliefRequest> requests;
        if (disasterId != null) {
            requests = reliefRequestRepository.findByDisasterId(disasterId, priorityPageable);
        } else if (status != null) {
            requests = reliefRequestRepository.findByStatus(status, priorityPageable);
        } else {
            requests = reliefRequestRepository.findAll(priorityPageable);
        }
        return requests.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ReliefRequestResponse> getPendingByPriority(int limit) {
        return reliefRequestRepository.findByPriorityScore(PageRequest.of(0, limit))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StatusHistory> getTimeline(Long requestId) {
        // Ensure request exists
        reliefRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("ReliefRequest", "id", requestId));
        return statusHistoryRepository.findByReliefRequestIdOrderByCreatedAtAsc(requestId);
    }

    @Transactional
    public ReliefRequestResponse createRequest(ReliefRequestDto dto) {
        Victim victim = victimRepository.findById(dto.getVictimId())
                .orElseThrow(() -> new ResourceNotFoundException("Victim", "id", dto.getVictimId()));
        Disaster disaster = disasterRepository.findById(dto.getDisasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Disaster", "id", dto.getDisasterId()));

        ReliefRequest request = ReliefRequest.builder()
                .victim(victim)
                .disaster(disaster)
                .requestType(dto.getRequestType())
                .description(dto.getDescription())
                .urgencyLevel(dto.getUrgencyLevel())
                .quantityNeeded(dto.getQuantityNeeded())
                .affectedPeople(dto.getAffectedPeople() != null ? dto.getAffectedPeople() : 1)
                .hasElderlyChildren(Boolean.TRUE.equals(dto.getHasElderlyChildren()))
                .isMedicalEmergency(Boolean.TRUE.equals(dto.getIsMedicalEmergency()))
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .locationName(dto.getLocationName())
                .status(ReliefRequest.RequestStatus.PENDING)
                .notes(dto.getNotes())
                .build();

        // Calculate initial priority score
        int score = priorityService.calculate(request);
        request.setPriorityScore(score);

        ReliefRequest saved = reliefRequestRepository.save(request);

        // Record initial status
        recordHistory(saved, null, ReliefRequest.RequestStatus.PENDING, "Request submitted");

        return toResponse(saved);
    }

    @Transactional
    public ReliefRequestResponse updateStatus(Long id, ReliefRequest.RequestStatus newStatus) {
        ReliefRequest request = reliefRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReliefRequest", "id", id));

        ReliefRequest.RequestStatus oldStatus = request.getStatus();
        request.setStatus(newStatus);

        if (newStatus == ReliefRequest.RequestStatus.FULFILLED) {
            request.setFulfilledAt(java.time.LocalDateTime.now());
        }

        // Recalculate priority after status change
        request.setPriorityScore(priorityService.calculate(request));

        ReliefRequest saved = reliefRequestRepository.save(request);

        // Record the status transition
        recordHistory(saved, oldStatus, newStatus, null);

        // Async audit log
        auditLogService.log("STATUS_CHANGE", "ReliefRequest", id,
                "{\"status\":\"" + oldStatus + "\"}",
                "{\"status\":\"" + newStatus + "\"}");

        return toResponse(saved);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void recordHistory(ReliefRequest request,
                                ReliefRequest.RequestStatus from,
                                ReliefRequest.RequestStatus to,
                                String comment) {
        String actor = currentUsername();
        statusHistoryRepository.save(StatusHistory.builder()
                .reliefRequest(request)
                .fromStatus(from)
                .toStatus(to)
                .changedBy(actor)
                .comment(comment)
                .build());
    }

    private String currentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : "system";
        } catch (Exception e) {
            return "system";
        }
    }

    private ReliefRequestResponse toResponse(ReliefRequest r) {
        return ReliefRequestResponse.builder()
                .id(r.getId())
                .victimId(r.getVictim().getId())
                .victimName(r.getVictim().getFullName())
                .victimPhone(r.getVictim().getPhone())
                .disasterId(r.getDisaster().getId())
                .disasterTitle(r.getDisaster().getTitle())
                .requestType(r.getRequestType())
                .description(r.getDescription())
                .urgencyLevel(r.getUrgencyLevel())
                .quantityNeeded(r.getQuantityNeeded())
                .status(r.getStatus())
                .priorityScore(r.getPriorityScore())
                .assignedVolunteerName(r.getAssignedTo() != null ? r.getAssignedTo().getUser().getFullName() : null)
                .fulfilledAt(r.getFulfilledAt())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .locationName(r.getLocationName())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .build();
    }
}
