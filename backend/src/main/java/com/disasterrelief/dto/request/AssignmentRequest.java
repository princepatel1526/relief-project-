package com.disasterrelief.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentRequest {
    @NotNull private Long volunteerId;
    @NotNull private Long disasterId;
    private Long reliefRequestId;
    private String notes;
}
