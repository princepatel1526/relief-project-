package com.disasterrelief.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryUpdateRequest {
    @NotNull
    private Integer quantityChange;

    @NotNull
    private OperationType operation;

    private String reason;

    public enum OperationType { ADD, SUBTRACT, SET }
}
