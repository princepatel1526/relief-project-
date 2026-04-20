package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.NewsUpdateRequest;
import com.disasterrelief.dto.response.NewsTimelineUpdateResponse;
import com.disasterrelief.dto.response.NewsUpdateResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.NewsTimelineUpdate;
import com.disasterrelief.entity.NewsUpdate;
import com.disasterrelief.entity.User;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.DisasterRepository;
import com.disasterrelief.repository.NewsUpdateRepository;
import com.disasterrelief.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl {

    private final NewsUpdateRepository newsUpdateRepository;
    private final DisasterRepository disasterRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<NewsUpdateResponse> list(NewsUpdate.NewsStatus status,
                                         Disaster.Severity severity,
                                         String disasterType,
                                         String query,
                                         Pageable pageable) {
        autoIngestDisastersIfNeeded();

        Page<NewsUpdate> page;
        if (query != null && !query.isBlank()) {
            page = newsUpdateRepository.search(query.trim(), pageable);
        } else if (status != null) {
            page = newsUpdateRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (severity != null) {
            page = newsUpdateRepository.findBySeverityOrderByCreatedAtDesc(severity, pageable);
        } else if (disasterType != null && !disasterType.isBlank()) {
            page = newsUpdateRepository.findByDisasterTypeContainingIgnoreCaseOrderByCreatedAtDesc(disasterType.trim(), pageable);
        } else {
            page = newsUpdateRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public NewsUpdateResponse getById(Long id) {
        autoIngestDisastersIfNeeded();
        NewsUpdate news = newsUpdateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        return toResponse(news);
    }

    @Transactional
    public NewsUpdateResponse create(NewsUpdateRequest request) {
        User currentUser = currentUser();
        NewsUpdate news = new NewsUpdate();
        apply(request, news, currentUser);
        return toResponse(newsUpdateRepository.save(news));
    }

    @Transactional
    public NewsUpdateResponse update(Long id, NewsUpdateRequest request) {
        User currentUser = currentUser();
        NewsUpdate news = newsUpdateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        apply(request, news, currentUser);
        return toResponse(newsUpdateRepository.save(news));
    }

    @Transactional
    public void delete(Long id) {
        NewsUpdate news = newsUpdateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        newsUpdateRepository.delete(news);
    }

    @Transactional
    protected void autoIngestDisastersIfNeeded() {
        if (newsUpdateRepository.count() > 0) {
            return;
        }

        List<Disaster> disasters = disasterRepository.findAll();
        if (disasters.isEmpty()) {
            return;
        }

        for (Disaster d : disasters) {
            if (d.getStatus() == Disaster.DisasterStatus.CLOSED) {
                continue;
            }
            if (d.getId() != null && newsUpdateRepository.existsBySourceIncidentId(d.getId())) {
                continue;
            }

            NewsUpdate news = NewsUpdate.builder()
                    .title(d.getTitle())
                    .summary(buildSummary(d.getDescription()))
                    .content(d.getDescription() != null ? d.getDescription() : "Incident reported and currently under monitoring.")
                    .imageUrl(defaultImageBySeverity(d.getSeverity()))
                    .disasterType(d.getDisasterType() != null ? d.getDisasterType().getName() : "General")
                    .severity(d.getSeverity())
                    .status(mapStatus(d.getStatus()))
                    .location(d.getLocationName())
                    .latitude(d.getLatitude())
                    .longitude(d.getLongitude())
                    .sourceIncidentId(d.getId())
                    .createdBy(d.getReportedBy())
                    .build();

            List<NewsTimelineUpdate> timeline = new ArrayList<>();
            timeline.add(NewsTimelineUpdate.builder()
                    .news(news)
                    .updateText("Incident registered in operations system.")
                    .timestamp(d.getCreatedAt() != null ? d.getCreatedAt() : LocalDateTime.now())
                    .build());
            if (d.getStatus() == Disaster.DisasterStatus.ACTIVE || d.getStatus() == Disaster.DisasterStatus.CONTAINED) {
                timeline.add(NewsTimelineUpdate.builder()
                        .news(news)
                        .updateText("Field response teams deployed and monitoring continues.")
                        .timestamp(LocalDateTime.now().minusHours(2))
                        .build());
            }
            news.setTimelineUpdates(timeline);
            newsUpdateRepository.save(news);
        }

        log.info("Auto-generated news feed from existing disasters");
    }

    private void apply(NewsUpdateRequest request, NewsUpdate news, User currentUser) {
        news.setTitle(request.getTitle());
        news.setSummary(request.getSummary());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        news.setDisasterType(request.getDisasterType());
        news.setSeverity(request.getSeverity());
        news.setStatus(request.getStatus());
        news.setLocation(request.getLocation());
        news.setLatitude(request.getLatitude());
        news.setLongitude(request.getLongitude());
        news.setSourceIncidentId(request.getSourceIncidentId());
        news.setCreatedBy(currentUser);

        if (news.getTimelineUpdates() == null) {
            news.setTimelineUpdates(new ArrayList<>());
        }
        news.getTimelineUpdates().clear();
        if (request.getTimelineUpdates() != null) {
            request.getTimelineUpdates().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(updateText -> news.getTimelineUpdates().add(
                            NewsTimelineUpdate.builder()
                                    .news(news)
                                    .updateText(updateText.trim())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    ));
        }
    }

    private NewsUpdate.NewsStatus mapStatus(Disaster.DisasterStatus status) {
        if (status == null) {
            return NewsUpdate.NewsStatus.MONITORING;
        }
        return switch (status) {
            case ACTIVE -> NewsUpdate.NewsStatus.ACTIVE;
            case RESOLVED, CLOSED -> NewsUpdate.NewsStatus.RESOLVED;
            default -> NewsUpdate.NewsStatus.MONITORING;
        };
    }

    private String buildSummary(String description) {
        if (description == null || description.isBlank()) {
            return "Operations teams are actively monitoring this developing incident.";
        }
        String normalized = description.trim();
        return normalized.length() <= 180 ? normalized : normalized.substring(0, 177) + "...";
    }

    private String defaultImageBySeverity(Disaster.Severity severity) {
        return switch (severity != null ? severity : Disaster.Severity.MEDIUM) {
            case CRITICAL -> "https://images.unsplash.com/photo-1489515217757-5fd1be406fef?auto=format&fit=crop&w=1200&q=80";
            case HIGH -> "https://images.unsplash.com/photo-1475776408506-9a5371e7a068?auto=format&fit=crop&w=1200&q=80";
            case MEDIUM -> "https://images.unsplash.com/photo-1454789548928-9efd52dc4031?auto=format&fit=crop&w=1200&q=80";
            case LOW -> "https://images.unsplash.com/photo-1482192596544-9eb780fc7f66?auto=format&fit=crop&w=1200&q=80";
        };
    }

    private User currentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
    }

    private NewsUpdateResponse toResponse(NewsUpdate n) {
        List<NewsTimelineUpdateResponse> timeline = n.getTimelineUpdates().stream()
                .sorted(Comparator.comparing(NewsTimelineUpdate::getTimestamp).reversed())
                .map(t -> NewsTimelineUpdateResponse.builder()
                        .id(t.getId())
                        .updateText(t.getUpdateText())
                        .timestamp(t.getTimestamp())
                        .build())
                .toList();

        return NewsUpdateResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .summary(n.getSummary())
                .content(n.getContent())
                .imageUrl(n.getImageUrl())
                .disasterType(n.getDisasterType())
                .severity(n.getSeverity())
                .status(n.getStatus())
                .location(n.getLocation())
                .latitude(n.getLatitude())
                .longitude(n.getLongitude())
                .sourceIncidentId(n.getSourceIncidentId())
                .createdBy(n.getCreatedBy() != null ? n.getCreatedBy().getId() : null)
                .createdByName(n.getCreatedBy() != null ? n.getCreatedBy().getFullName() : "System")
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .timelineUpdates(timeline)
                .build();
    }
}
