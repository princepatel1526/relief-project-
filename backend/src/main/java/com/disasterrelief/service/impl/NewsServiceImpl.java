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
import org.springframework.data.domain.PageImpl;
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
                                         String region,
                                         String viewSort,
                                         String query,
                                         Pageable pageable) {
        //syncFromDisasters();

        Page<NewsUpdate> page;
        if (query != null && !query.isBlank()) {
            page = newsUpdateRepository.search(query.trim(), pageable);
        } else if (region != null && !region.isBlank()) {
            page = newsUpdateRepository.findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(region.trim(), pageable);
        } else if (status != null) {
            page = newsUpdateRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (severity != null) {
            page = newsUpdateRepository.findBySeverityOrderByCreatedAtDesc(severity, pageable);
        } else if (disasterType != null && !disasterType.isBlank()) {
            page = newsUpdateRepository.findByDisasterTypeContainingIgnoreCaseOrderByCreatedAtDesc(disasterType.trim(), pageable);
        } else {
            page = newsUpdateRepository.findAll(pageable);
        }

        List<NewsUpdate> ordered = new ArrayList<>(page.getContent());
        if ("ACTIVE_FIRST".equalsIgnoreCase(viewSort)) {
            ordered.sort((a, b) -> Boolean.compare(a.getStatus() != NewsUpdate.NewsStatus.ACTIVE, b.getStatus() != NewsUpdate.NewsStatus.ACTIVE));
        } else if ("CRITICAL_FIRST".equalsIgnoreCase(viewSort)) {
            ordered.sort((a, b) -> severityRank(a.getSeverity()) - severityRank(b.getSeverity()));
        } else {
            ordered.sort(Comparator.comparing(NewsUpdate::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        return new PageImpl<>(ordered.stream().map(this::toResponse).toList(), pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public NewsUpdateResponse getById(Long id) {
        //syncFromDisasters();
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
    protected void syncFromDisasters() {
        List<Disaster> disasters = disasterRepository.findAll();
        if (disasters.isEmpty()) return;

        List<NewsUpdate> existing = newsUpdateRepository.findAll();
        for (Disaster d : disasters) {
            if (d.getStatus() == Disaster.DisasterStatus.CLOSED || d.getStatus() == Disaster.DisasterStatus.REPORTED) continue;

            NewsUpdate news = existing.stream()
                    .filter(n -> n.getSourceIncidentId() != null && n.getSourceIncidentId().equals(d.getId()))
                    .findFirst()
                    .orElseGet(NewsUpdate::new);

            news.setTitle(d.getTitle());
            news.setSummary(buildSummary(d.getDescription(), d.getAffectedPeople(), d.getLocationName()));
            news.setContent(generateArticle(d));
            news.setImageUrl(defaultImageByType(d.getDisasterType() != null ? d.getDisasterType().getName() : null));
            news.setDisasterType(d.getDisasterType() != null ? d.getDisasterType().getName() : "General");
            news.setSeverity(d.getSeverity());
            news.setStatus(mapStatus(d.getStatus()));
            news.setLocation(d.getLocationName());
            news.setLatitude(d.getLatitude());
            news.setLongitude(d.getLongitude());
            news.setSourceIncidentId(d.getId());
            news.setCreatedBy(d.getReportedBy());
            news.setAffectedPeople(d.getAffectedPeople());
            news.setRescueProgress(progressFromStatus(d.getStatus()));

            if (news.getTimelineUpdates() == null || news.getTimelineUpdates().isEmpty()) {
                List<NewsTimelineUpdate> timeline = new ArrayList<>();
                timeline.add(NewsTimelineUpdate.builder()
                        .news(news)
                        .updateText("Incident verified and entered into the live operations feed.")
                        .timestamp(d.getCreatedAt() != null ? d.getCreatedAt() : LocalDateTime.now())
                        .build());
                timeline.add(NewsTimelineUpdate.builder()
                        .news(news)
                        .updateText("Rescue coordination teams deployed to affected sectors.")
                        .timestamp(LocalDateTime.now().minusHours(1))
                        .build());
                news.setTimelineUpdates(timeline);
            }

            newsUpdateRepository.save(news);
        }
    }

    private void apply(NewsUpdateRequest request, NewsUpdate news, User currentUser) {
        news.setTitle(request.getTitle());
        news.setSummary(request.getSummary());
        news.setContent(request.getContent());
        news.setImageUrl((request.getImageUrl() == null || request.getImageUrl().isBlank())
                ? defaultImageByType(request.getDisasterType())
                : request.getImageUrl());
        news.setDisasterType(request.getDisasterType());
        news.setSeverity(request.getSeverity());
        news.setStatus(request.getStatus());
        news.setLocation(request.getLocation());
        news.setLatitude(request.getLatitude());
        news.setLongitude(request.getLongitude());
        news.setSourceIncidentId(request.getSourceIncidentId());
        news.setAffectedPeople(request.getAffectedPeople());
        news.setRescueProgress(request.getRescueProgress());
        news.setCreatedBy(currentUser);

        if (news.getTimelineUpdates() == null) news.setTimelineUpdates(new ArrayList<>());
        news.getTimelineUpdates().clear();
        if (request.getTimelineUpdates() != null) {
            request.getTimelineUpdates().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(updateText -> news.getTimelineUpdates().add(
                            NewsTimelineUpdate.builder()
                                    .news(news)
                                    .updateText(updateText.trim())
                                    .timestamp(LocalDateTime.now())
                                    .build()));
        }
    }

    private NewsUpdate.NewsStatus mapStatus(Disaster.DisasterStatus status) {
        if (status == null) return NewsUpdate.NewsStatus.MONITORING;
        return switch (status) {
            case ACTIVE -> NewsUpdate.NewsStatus.ACTIVE;
            case RESOLVED, CLOSED -> NewsUpdate.NewsStatus.RESOLVED;
            default -> NewsUpdate.NewsStatus.MONITORING;
        };
    }

    private String buildSummary(String description, Integer affectedPeople, String locationName) {
        if (description != null && !description.isBlank()) {
            String normalized = description.trim();
            return normalized.length() <= 180 ? normalized : normalized.substring(0, 177) + "...";
        }
        String people = (affectedPeople != null && affectedPeople > 0) ? (affectedPeople + " people") : "multiple residents";
        String location = (locationName != null && !locationName.isBlank()) ? locationName : "the impacted region";
        return "Emergency response teams are monitoring " + location + " where " + people + " may be affected.";
    }

    private String generateArticle(Disaster d) {
        String type = d.getDisasterType() != null ? d.getDisasterType().getName() : "incident";
        String location = d.getLocationName() != null ? d.getLocationName() : "the affected region";
        int affected = d.getAffectedPeople() != null ? d.getAffectedPeople() : 0;
        String base = d.getDescription() != null && !d.getDescription().isBlank()
                ? d.getDescription()
                : "Operations teams are coordinating response across field units and support partners.";
        return """
                %s

                Incident intelligence indicates a %s event centered around %s. Current severity is %s and status is %s.
                Field coordinators have mobilized rescue, shelter, and medical channels while logistics teams prioritize supplies.
                Estimated affected population currently stands at %d, and this figure will be updated as verification continues.

                Citizens are advised to follow official emergency advisories, avoid blocked routes, and use verified helplines.
                """.formatted(base, type, location, d.getSeverity(), d.getStatus(), affected);
    }

    private int progressFromStatus(Disaster.DisasterStatus status) {
        return switch (status) {
            case ACTIVE -> 55;
            case CONTAINED -> 75;
            case RESOLVED -> 100;
            default -> 35;
        };
    }

    private int severityRank(Disaster.Severity severity) {
        return switch (severity != null ? severity : Disaster.Severity.LOW) {
            case CRITICAL -> 0;
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }

    private String defaultImageByType(String typeRaw) {
        String type = typeRaw == null ? "" : typeRaw.toLowerCase();
        if (type.contains("flood")) return "/assets/news/flood.svg";
        if (type.contains("fire")) return "/assets/news/fire.svg";
        if (type.contains("landslide")) return "/assets/news/landslide.svg";
        if (type.contains("medical")) return "/assets/news/medical.svg";
        if (type.contains("cyclone")) return "/assets/news/cyclone.svg";
        if (type.contains("shelter")) return "/assets/news/shelter.svg";
        return "/assets/news/fallback.svg";
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
                .affectedPeople(n.getAffectedPeople())
                .rescueProgress(n.getRescueProgress())
                .createdBy(n.getCreatedBy() != null ? n.getCreatedBy().getId() : null)
                .createdByName(n.getCreatedBy() != null ? n.getCreatedBy().getFullName() : "System")
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .timelineUpdates(timeline)
                .build();
    }
}
