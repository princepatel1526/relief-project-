package com.disasterrelief.dto.response;

import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.NewsUpdate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NewsUpdateResponse {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private String disasterType;
    private Disaster.Severity severity;
    private NewsUpdate.NewsStatus status;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long sourceIncidentId;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<NewsTimelineUpdateResponse> timelineUpdates;
}
