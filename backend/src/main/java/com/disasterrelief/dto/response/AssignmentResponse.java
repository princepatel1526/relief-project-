package com.disasterrelief.dto.response;

import com.disasterrelief.entity.Assignment;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class AssignmentResponse {
    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private Long disasterId;
    private String disasterTitle;
    private Long reliefRequestId;
    private String assignedByName;
    private Assignment.AssignmentStatus status;
    private String notes;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private Double hoursLogged;
}
