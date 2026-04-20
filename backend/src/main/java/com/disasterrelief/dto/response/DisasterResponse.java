package com.disasterrelief.dto.response;

import com.disasterrelief.entity.Disaster;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class DisasterResponse {
    private Long id;
    private String title;
    private String description;
    private String disasterTypeName;
    private String disasterTypeIcon;
    private Disaster.Severity severity;
    private Disaster.DisasterStatus status;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private Double affectedAreaKm;
    private Integer affectedPeople;
    private String reportedByName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private Long activeRequestsCount;
}
