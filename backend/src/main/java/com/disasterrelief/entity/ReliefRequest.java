package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relief_requests",
    indexes = {
        @Index(name = "idx_relief_status",   columnList = "status"),
        @Index(name = "idx_relief_urgency",  columnList = "urgency_level"),
        @Index(name = "idx_relief_disaster", columnList = "disaster_id"),
        @Index(name = "idx_relief_priority", columnList = "priority_score")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReliefRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "victim_id", nullable = false)
    private Victim victim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id", nullable = false)
    private Disaster disaster;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "urgency_level", nullable = false)
    private Integer urgencyLevel;

    @Column(name = "quantity_needed")
    private Integer quantityNeeded;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private Volunteer assignedTo;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "priority_score")
    @Builder.Default
    private Integer priorityScore = 0;

    @Column(name = "affected_people")
    @Builder.Default
    private Integer affectedPeople = 1;

    @Column(name = "has_elderly_children")
    @Builder.Default
    private Boolean hasElderlyChildren = false;

    @Column(name = "is_medical_emergency")
    @Builder.Default
    private Boolean isMedicalEmergency = false;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(columnDefinition = "TEXT")
    private String notes;

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

    public enum RequestType { FOOD, WATER, MEDICAL, SHELTER, CLOTHING, RESCUE, OTHER }
    public enum RequestStatus {
        SUBMITTED,          // Citizen just submitted
        PENDING_VERIFICATION, // Admin reviewing
        VERIFIED,           // Admin confirmed genuine
        ASSIGNED,           // Volunteer/team assigned
        EN_ROUTE,           // Team heading to site
        IN_PROGRESS,        // Active relief ongoing
        FULFILLED,          // Request fully resolved
        REJECTED,           // Duplicate or invalid request
        // Legacy aliases kept for backward compatibility
        PENDING, CANCELLED
    }
}
