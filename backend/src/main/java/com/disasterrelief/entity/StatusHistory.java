package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "status_history",
    indexes = {
        @Index(name = "idx_status_history_request", columnList = "request_id"),
        @Index(name = "idx_status_history_created", columnList = "created_at")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ReliefRequest reliefRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private ReliefRequest.RequestStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private ReliefRequest.RequestStatus toStatus;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}
