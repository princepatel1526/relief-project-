package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "victims",
    indexes = {
        @Index(name = "idx_victims_disaster", columnList = "disaster_id"),
        @Index(name = "idx_victims_status", columnList = "status")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Victim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private Double latitude;

    private Double longitude;

    @Column(name = "family_size")
    @Builder.Default
    private Integer familySize = 1;

    @Column(name = "special_needs", columnDefinition = "TEXT")
    private String specialNeeds;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VictimStatus status = VictimStatus.REGISTERED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id", nullable = false)
    private Disaster disaster;

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

    public enum VictimStatus { REGISTERED, SHELTERED, RECEIVING_AID, EVACUATED, RECOVERED }
}
