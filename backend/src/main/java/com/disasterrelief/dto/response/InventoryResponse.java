package com.disasterrelief.dto.response;

import com.disasterrelief.entity.Inventory;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class InventoryResponse {
    private Long id;
    private String itemName;
    private Inventory.InventoryCategory category;
    private Integer quantity;
    private String unit;
    private String locationName;
    private String disasterTitle;
    private Integer minThreshold;
    private boolean lowStock;
    private LocalDate expiryDate;
    private LocalDateTime updatedAt;
}
