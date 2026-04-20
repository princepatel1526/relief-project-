package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "disasters",
    indexes = {
        @Index(name = "idx_disasters_status", columnList = "status"),
        @Index(name = "idx_disasters_severity", columnList = "severity"),
        @Index(name = "idx_disasters_lat_lng", columnList = "latitude,longitude")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Disaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disaster_type_id", nullable = false)
    private DisasterType disasterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Severity severity = Severity.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DisasterStatus status = DisasterStatus.REPORTED;

    private Double latitude;

    private Double longitude;

    @Column(name = "location_name", length = 200)
    private String locationName;


    private Double affectedAreaKm;

    @Column(name = "affected_people")
    @Builder.Default
    private Integer affectedPeople = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }
    public enum DisasterStatus { REPORTED, ACTIVE, CONTAINED, RESOLVED, CLOSED }
}
