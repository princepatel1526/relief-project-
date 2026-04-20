package com.disasterrelief.dto.request;

import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.NewsUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewsUpdateRequest {

    @NotBlank
    @Size(max = 220)
    private String title;

    @NotBlank
    @Size(max = 400)
    private String summary;

    @NotBlank
    private String content;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 100)
    private String disasterType;

    @NotNull
    private Disaster.Severity severity;

    @NotNull
    private NewsUpdate.NewsStatus status;

    @Size(max = 200)
    private String location;

    private Double latitude;

    private Double longitude;

    private Long sourceIncidentId;

    private Integer affectedPeople;

    private Integer rescueProgress;

    private List<@NotBlank String> timelineUpdates;
}
