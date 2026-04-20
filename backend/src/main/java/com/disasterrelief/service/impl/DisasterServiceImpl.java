package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.DisasterRequest;
import com.disasterrelief.dto.response.DisasterResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.DisasterType;
import com.disasterrelief.entity.User;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.DisasterRepository;
import com.disasterrelief.repository.DisasterTypeRepository;
import com.disasterrelief.repository.ReliefRequestRepository;
import com.disasterrelief.repository.UserRepository;
import com.disasterrelief.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisasterServiceImpl {

    private final DisasterRepository disasterRepository;
    private final DisasterTypeRepository disasterTypeRepository;
    private final UserRepository userRepository;
    private final ReliefRequestRepository reliefRequestRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<DisasterResponse> getAllDisasters(Disaster.DisasterStatus status,
                                                   Disaster.Severity severity,
                                                   Pageable pageable) {
        Page<Disaster> disasters;
        if (status != null && severity != null) {
            disasters = disasterRepository.findByStatusAndSeverity(status, severity, pageable);
        } else if (status != null) {
            disasters = disasterRepository.findByStatus(status, pageable);
        } else if (severity != null) {
            disasters = disasterRepository.findBySeverity(severity, pageable);
        } else {
            disasters = disasterRepository.findAll(pageable);
        }
        return disasters.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DisasterResponse getDisasterById(Long id) {
        Disaster disaster = findById(id);
        return toResponse(disaster);
    }

    @Transactional
    public DisasterResponse createDisaster(DisasterRequest request) {
        DisasterType type = disasterTypeRepository.findById(request.getDisasterTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("DisasterType", "id", request.getDisasterTypeId()));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        Disaster disaster = Disaster.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .disasterType(type)
                .severity(request.getSeverity())
                .status(Disaster.DisasterStatus.REPORTED)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .locationName(request.getLocationName())
                .affectedAreaKm(request.getAffectedAreaKm())
                .affectedPeople(request.getAffectedPeople() != null ? request.getAffectedPeople() : 0)
                .reportedBy(reporter)
                .startDate(request.getStartDate())
                .build();

        disaster = disasterRepository.save(disaster);
        log.info("Disaster created: id={}, title={}, severity={}", disaster.getId(), disaster.getTitle(), disaster.getSeverity());

        notificationService.broadcastDisasterAlert(disaster);
        return toResponse(disaster);
    }

    @Transactional
    public DisasterResponse updateDisaster(Long id, DisasterRequest request) {
        Disaster disaster = findById(id);
        DisasterType type = disasterTypeRepository.findById(request.getDisasterTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("DisasterType", "id", request.getDisasterTypeId()));

        disaster.setTitle(request.getTitle());
        disaster.setDescription(request.getDescription());
        disaster.setDisasterType(type);
        disaster.setSeverity(request.getSeverity());
        disaster.setLatitude(request.getLatitude());
        disaster.setLongitude(request.getLongitude());
        disaster.setLocationName(request.getLocationName());
        disaster.setAffectedAreaKm(request.getAffectedAreaKm());
        if (request.getAffectedPeople() != null) disaster.setAffectedPeople(request.getAffectedPeople());

        return toResponse(disasterRepository.save(disaster));
    }

    @Transactional
    public DisasterResponse updateStatus(Long id, Disaster.DisasterStatus newStatus) {
        Disaster disaster = findById(id);
        disaster.setStatus(newStatus);
        log.info("Disaster {} status updated to {}", id, newStatus);
        return toResponse(disasterRepository.save(disaster));
    }

    @Transactional(readOnly = true)
    public List<DisasterResponse> getNearbyDisasters(double lat, double lng, double radiusKm) {
        return disasterRepository.findActiveDisastersNearby(lat, lng, radiusKm)
                .stream().map(this::toResponse).toList();
    }

    private Disaster findById(Long id) {
        return disasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disaster", "id", id));
    }

    private DisasterResponse toResponse(Disaster d) {
        long activeRequests = reliefRequestRepository.countByDisasterIdAndStatus(
                d.getId(), com.disasterrelief.entity.ReliefRequest.RequestStatus.PENDING);
        return DisasterResponse.builder()
                .id(d.getId())
                .title(d.getTitle())
                .description(d.getDescription())
                .disasterTypeName(d.getDisasterType().getName())
                .disasterTypeIcon(d.getDisasterType().getIcon())
                .severity(d.getSeverity())
                .status(d.getStatus())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .locationName(d.getLocationName())
                .affectedAreaKm(d.getAffectedAreaKm())
                .affectedPeople(d.getAffectedPeople())
                .reportedByName(d.getReportedBy().getFullName())
                .startDate(d.getStartDate())
                .endDate(d.getEndDate())
                .createdAt(d.getCreatedAt())
                .activeRequestsCount(activeRequests)
                .build();
    }
}
