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

    @NotNull @Min(1) @Max(10)
    private Integer urgencyLevel;

    @Min(1)
    private Integer quantityNeeded;

    private Integer affectedPeople;
    private Boolean hasElderlyChildren;
    private Boolean isMedicalEmergency;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    @Size(max = 200)
    private String locationName;

    private String notes;
}
