package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
    indexes = {
        @Index(name = "idx_inventory_category", columnList = "category"),
        @Index(name = "idx_inventory_disaster", columnList = "disaster_id")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryCategory category;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String unit = "units";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_id")
    private Disaster disaster;

    @Column(name = "min_threshold")
    @Builder.Default
    private Integer minThreshold = 10;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Version
    private Long version;

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

    public boolean isLowStock() {
        return this.quantity <= this.minThreshold;
    }

    public enum InventoryCategory { FOOD, WATER, MEDICINE, CLOTHING, EQUIPMENT, SHELTER, OTHER }
}
