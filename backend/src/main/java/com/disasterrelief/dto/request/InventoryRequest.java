package com.disasterrelief.dto.request;

import com.disasterrelief.entity.Inventory;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class InventoryRequest {
    @NotBlank @Size(max = 200)
    private String itemName;

    @NotNull
    private Inventory.InventoryCategory category;

    @NotNull @Min(0)
    private Integer quantity;

    @NotBlank @Size(max = 50)
    private String unit;

    private Long locationId;
    private Long disasterId;

    @Min(0)
    private Integer minThreshold = 10;

    private LocalDate expiryDate;
}
