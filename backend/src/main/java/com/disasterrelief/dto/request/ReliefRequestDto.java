package com.disasterrelief.dto.request;

import com.disasterrelief.entity.ReliefRequest;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReliefRequestDto {
    @NotNull private Long victimId;
    @NotNull private Long disasterId;
    @NotNull private ReliefRequest.RequestType requestType;

    private String description;

    @NotNull @Min(1) @Max(5)
    private Integer urgencyLevel;

    @Min(1)
    private Integer quantityNeeded;

    private String notes;
}
