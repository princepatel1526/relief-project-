package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations",
    indexes = {
        @Index(name = "idx_donations_status", columnList = "status"),
        @Index(name = "idx_donations_disaster", columnList = "disaster_id")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id")
    private Disaster disaster;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_type")
    @Builder.Default
    private DonationType donationType = DonationType.MONETARY;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DonationStatus status = DonationStatus.PENDING;

    @Column(name = "donor_name", nullable = false, length = 100)
    private String donorName;

    @Column(name = "donor_email", length = 100)
    private String donorEmail;

    @Column(name = "donor_phone", length = 20)
    private String donorPhone;

    @Column(name = "is_anonymous")
    @Builder.Default
    private Boolean isAnonymous = false;

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

    public enum DonationType { MONETARY, FOOD, CLOTHING, MEDICINE, EQUIPMENT, OTHER }
    public enum DonationStatus { PENDING, CONFIRMED, DISTRIBUTED, CANCELLED }
}
