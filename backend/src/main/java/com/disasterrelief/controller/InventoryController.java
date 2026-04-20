package com.disasterrelief.controller;

import com.disasterrelief.dto.request.InventoryRequest;
import com.disasterrelief.dto.request.InventoryUpdateRequest;
import com.disasterrelief.dto.response.InventoryResponse;
import com.disasterrelief.entity.Inventory;
import com.disasterrelief.service.impl.InventoryServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
public class InventoryController {

    private final InventoryServiceImpl inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> getAllInventory(
            @RequestParam(required = false) Long disasterId,
            @RequestParam(required = false) Inventory.InventoryCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventory(disasterId, category, pageable));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getAllInventory(null, null,
                org.springframework.data.domain.PageRequest.of(0, 1))
                .getContent().stream().filter(i -> i.getId().equals(id)).findFirst().orElseThrow());
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createItem(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createItem(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateItem(@PathVariable Long id,
                                                         @Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateItem(id, request));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<InventoryResponse> updateQuantity(@PathVariable Long id,
                                                             @Valid @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateQuantity(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
