package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.AssignmentRequest;
import com.disasterrelief.dto.response.AssignmentResponse;
import com.disasterrelief.entity.*;
import com.disasterrelief.exception.BusinessException;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.*;
import com.disasterrelief.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl {

    private final AssignmentRepository assignmentRepository;
    private final VolunteerRepository volunteerRepository;
    private final DisasterRepository disasterRepository;
    private final ReliefRequestRepository reliefRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        Volunteer volunteer = volunteerRepository.findById(request.getVolunteerId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", request.getVolunteerId()));

        if (volunteer.getAvailability() == Volunteer.Availability.UNAVAILABLE) {
            throw new BusinessException("VOLUNTEER_UNAVAILABLE", "Volunteer is currently unavailable");
        }

        if (request.getReliefRequestId() != null) {
            boolean alreadyAssigned = assignmentRepository.existsByVolunteerIdAndReliefRequestIdAndStatusIn(
                    request.getVolunteerId(), request.getReliefRequestId(),
                    List.of(Assignment.AssignmentStatus.ASSIGNED, Assignment.AssignmentStatus.ACCEPTED,
                            Assignment.AssignmentStatus.IN_PROGRESS));
            if (alreadyAssigned) {
                throw new BusinessException("DUPLICATE_ASSIGNMENT",
                        "Volunteer is already assigned to this relief request");
            }
        }

        Disaster disaster = disasterRepository.findById(request.getDisasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Disaster", "id", request.getDisasterId()));

        ReliefRequest reliefRequest = null;
        if (request.getReliefRequestId() != null) {
            reliefRequest = reliefRequestRepository.findById(request.getReliefRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("ReliefRequest", "id", request.getReliefRequestId()));
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User assignedBy = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        Assignment assignment = Assignment.builder()
                .volunteer(volunteer)
                .disaster(disaster)
                .reliefRequest(reliefRequest)
                .assignedBy(assignedBy)
                .status(Assignment.AssignmentStatus.ASSIGNED)
                .notes(request.getNotes())
                .build();

        assignment = assignmentRepository.save(assignment);

        volunteer.setAvailability(Volunteer.Availability.BUSY);
        volunteerRepository.save(volunteer);

        if (reliefRequest != null) {
            reliefRequest.setStatus(ReliefRequest.RequestStatus.ASSIGNED);
            reliefRequest.setAssignedTo(volunteer);
            reliefRequestRepository.save(reliefRequest);
        }

        notificationService.notifyVolunteerAssignment(assignment);
        log.info("Assignment created: volunteer={}, disaster={}", volunteer.getId(), disaster.getId());

        return toResponse(assignment);
    }

    @Transactional
    public AssignmentResponse updateStatus(Long id, Assignment.AssignmentStatus newStatus, Double hoursLogged) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        assignment.setStatus(newStatus);

        if (newStatus == Assignment.AssignmentStatus.ACCEPTED) {
            assignment.setAcceptedAt(LocalDateTime.now());
        } else if (newStatus == Assignment.AssignmentStatus.COMPLETED) {
            assignment.setCompletedAt(LocalDateTime.now());
            if (hoursLogged != null) {
                assignment.setHoursLogged(java.math.BigDecimal.valueOf(hoursLogged));
                Volunteer v = assignment.getVolunteer();
                v.setTotalHours(v.getTotalHours().add(java.math.BigDecimal.valueOf(hoursLogged)));
                volunteerRepository.save(v);
            }
            assignment.getVolunteer().setAvailability(Volunteer.Availability.AVAILABLE);
            volunteerRepository.save(assignment.getVolunteer());

            if (assignment.getReliefRequest() != null) {
                assignment.getReliefRequest().setStatus(ReliefRequest.RequestStatus.FULFILLED);
                assignment.getReliefRequest().setFulfilledAt(LocalDateTime.now());
                reliefRequestRepository.save(assignment.getReliefRequest());
            }
        } else if (newStatus == Assignment.AssignmentStatus.DECLINED || newStatus == Assignment.AssignmentStatus.CANCELLED) {
            assignment.getVolunteer().setAvailability(Volunteer.Availability.AVAILABLE);
            volunteerRepository.save(assignment.getVolunteer());
            if (assignment.getReliefRequest() != null) {
                assignment.getReliefRequest().setStatus(ReliefRequest.RequestStatus.PENDING);
                assignment.getReliefRequest().setAssignedTo(null);
                reliefRequestRepository.save(assignment.getReliefRequest());
            }
        }

        return toResponse(assignmentRepository.save(assignment));
    }

    @Transactional(readOnly = true)
    public Page<AssignmentResponse> getAssignmentsByDisaster(Long disasterId, Pageable pageable) {
        return assignmentRepository.findByDisasterId(disasterId, pageable).map(this::toResponse);
    }

    private AssignmentResponse toResponse(Assignment a) {
        return AssignmentResponse.builder()
                .id(a.getId())
                .volunteerId(a.getVolunteer().getId())
                .volunteerName(a.getVolunteer().getUser().getFullName())
                .disasterId(a.getDisaster().getId())
                .disasterTitle(a.getDisaster().getTitle())
                .reliefRequestId(a.getReliefRequest() != null ? a.getReliefRequest().getId() : null)
                .assignedByName(a.getAssignedBy().getFullName())
                .status(a.getStatus())
                .notes(a.getNotes())
                .assignedAt(a.getAssignedAt())
                .acceptedAt(a.getAcceptedAt())
                .completedAt(a.getCompletedAt())
                .hoursLogged(a.getHoursLogged())
                .build();
    }
}
