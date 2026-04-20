package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.ReliefRequestDto;
import com.disasterrelief.dto.response.ReliefRequestResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.ReliefRequest;
import com.disasterrelief.entity.Victim;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.DisasterRepository;
import com.disasterrelief.repository.ReliefRequestRepository;
import com.disasterrelief.repository.VictimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReliefRequestServiceImpl {

    private final ReliefRequestRepository reliefRequestRepository;
    private final VictimRepository victimRepository;
    private final DisasterRepository disasterRepository;

    @Transactional(readOnly = true)
    public Page<ReliefRequestResponse> getAllRequests(Long disasterId,
                                                       ReliefRequest.RequestStatus status,
                                                       Pageable pageable) {
        // Override sort to always include urgency_level DESC for priority ordering
        Pageable priorityPageable = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "urgencyLevel").and(Sort.by(Sort.Direction.ASC, "createdAt")));

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
        return reliefRequestRepository.findPendingRequestsByPriority(PageRequest.of(0, limit))
                .stream().map(this::toResponse).toList();
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
                .status(ReliefRequest.RequestStatus.PENDING)
                .notes(dto.getNotes())
                .build();

        return toResponse(reliefRequestRepository.save(request));
    }

    @Transactional
    public ReliefRequestResponse updateStatus(Long id, ReliefRequest.RequestStatus status) {
        ReliefRequest request = reliefRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReliefRequest", "id", id));
        request.setStatus(status);
        if (status == ReliefRequest.RequestStatus.FULFILLED) {
            request.setFulfilledAt(java.time.LocalDateTime.now());
        }
        return toResponse(reliefRequestRepository.save(request));
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
                .assignedVolunteerName(r.getAssignedTo() != null ? r.getAssignedTo().getUser().getFullName() : null)
                .fulfilledAt(r.getFulfilledAt())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
