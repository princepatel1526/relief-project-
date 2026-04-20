package com.disasterrelief.dto.response;

import com.disasterrelief.entity.ReliefRequest;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ReliefRequestResponse {
    private Long id;
    private Long victimId;
    private String victimName;
    private String victimPhone;
    private Long disasterId;
    private String disasterTitle;
    private ReliefRequest.RequestType requestType;
    private String description;
    private Integer urgencyLevel;
    private Integer quantityNeeded;
    private ReliefRequest.RequestStatus status;
    private String assignedVolunteerName;
    private LocalDateTime fulfilledAt;
    private String notes;
    private LocalDateTime createdAt;
}
