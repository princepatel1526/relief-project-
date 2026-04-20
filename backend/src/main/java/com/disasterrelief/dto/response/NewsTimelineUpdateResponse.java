package com.disasterrelief.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NewsTimelineUpdateResponse {
    private Long id;
    private String updateText;
    private LocalDateTime timestamp;
}
