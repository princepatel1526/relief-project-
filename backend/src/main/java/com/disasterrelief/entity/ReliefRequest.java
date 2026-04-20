package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relief_requests",
    indexes = {
        @Index(name = "idx_relief_status", columnList = "status"),
        @Index(name = "idx_relief_urgency", columnList = "urgency_level"),
        @Index(name = "idx_relief_disaster", columnList = "disaster_id")
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
    public enum RequestStatus { PENDING, ASSIGNED, IN_PROGRESS, FULFILLED, CANCELLED }
}
