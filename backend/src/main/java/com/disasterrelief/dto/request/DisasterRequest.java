package com.disasterrelief.dto.request;

import com.disasterrelief.entity.Disaster;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DisasterRequest {
    @NotBlank @Size(max = 200)
    private String title;

    private String description;

    @NotNull
    private Long disasterTypeId;

    @NotNull
    private Disaster.Severity severity;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    @Size(max = 200)
    private String locationName;

    private Double affectedAreaKm;
    private Integer affectedPeople;
    private LocalDateTime startDate;
}
