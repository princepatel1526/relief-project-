package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments",
    indexes = {
        @Index(name = "idx_assignments_volunteer", columnList = "volunteer_id"),
        @Index(name = "idx_assignments_disaster", columnList = "disaster_id"),
        @Index(name = "idx_assignments_status", columnList = "status")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id", nullable = false)
    private Disaster disaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relief_request_id")
    private ReliefRequest reliefRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "hours_logged")
    private Double hoursLogged;

    @PrePersist
    void prePersist() { this.assignedAt = LocalDateTime.now(); }

    public enum AssignmentStatus { ASSIGNED, ACCEPTED, DECLINED, IN_PROGRESS, COMPLETED, CANCELLED }
}
