package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.InventoryRequest;
import com.disasterrelief.dto.request.InventoryUpdateRequest;
import com.disasterrelief.dto.response.InventoryResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.Inventory;
import com.disasterrelief.entity.Location;
import com.disasterrelief.exception.BusinessException;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.DisasterRepository;
import com.disasterrelief.repository.InventoryRepository;
import com.disasterrelief.repository.LocationRepository;
import com.disasterrelief.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl {

    private final InventoryRepository inventoryRepository;
    private final LocationRepository locationRepository;
    private final DisasterRepository disasterRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public InventoryResponse getItemById(Long id) {
        return toResponse(inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id)));
    }

    @Transactional(readOnly = true)
    public Page<InventoryResponse> getAllInventory(Long disasterId, Inventory.InventoryCategory category, Pageable pageable) {
        Page<Inventory> items;
        if (disasterId != null && category != null) {
            items = inventoryRepository.findByDisasterIdAndCategory(disasterId, category, pageable);
        } else if (disasterId != null) {
            items = inventoryRepository.findByDisasterId(disasterId, pageable);
        } else if (category != null) {
            items = inventoryRepository.findByCategory(category, pageable);
        } else {
            items = inventoryRepository.findAll(pageable);
        }
        return items.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream().map(this::toResponse).toList();
    }

    @Transactional
    public InventoryResponse createItem(InventoryRequest request) {
        Location location = request.getLocationId() != null
                ? locationRepository.findById(request.getLocationId()).orElse(null)
                : null;
        Disaster disaster = request.getDisasterId() != null
                ? disasterRepository.findById(request.getDisasterId()).orElse(null)
                : null;

        Inventory item = Inventory.builder()
                .itemName(request.getItemName())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .location(location)
                .disaster(disaster)
                .minThreshold(request.getMinThreshold())
                .expiryDate(request.getExpiryDate())
                .build();

        return toResponse(inventoryRepository.save(item));
    }

    @Transactional
    public InventoryResponse updateQuantity(Long id, InventoryUpdateRequest request) {
        Inventory item = inventoryRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        int newQuantity = switch (request.getOperation()) {
            case ADD      -> item.getQuantity() + request.getQuantityChange();
            case SUBTRACT -> item.getQuantity() - request.getQuantityChange();
            case SET      -> request.getQuantityChange();
        };

        if (newQuantity < 0) {
            throw new BusinessException("INSUFFICIENT_STOCK",
                    "Insufficient stock for " + item.getItemName() +
                    ". Available: " + item.getQuantity() + ", Requested: " + request.getQuantityChange());
        }

        item.setQuantity(newQuantity);
        Inventory saved = inventoryRepository.save(item);

        if (saved.isLowStock()) {
            log.warn("Low stock alert: {} (qty={}, threshold={})", saved.getItemName(), saved.getQuantity(), saved.getMinThreshold());
            notificationService.sendLowStockAlert(saved);
        }

        return toResponse(saved);
    }

    @Transactional
    public InventoryResponse updateItem(Long id, InventoryRequest request) {
        Inventory item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        Location location = request.getLocationId() != null
                ? locationRepository.findById(request.getLocationId()).orElse(item.getLocation())
                : null;
        Disaster disaster = request.getDisasterId() != null
                ? disasterRepository.findById(request.getDisasterId()).orElse(item.getDisaster())
                : null;

        item.setItemName(request.getItemName());
        item.setCategory(request.getCategory());
        item.setUnit(request.getUnit());
        item.setLocation(location);
        item.setDisaster(disaster);
        item.setMinThreshold(request.getMinThreshold());
        item.setExpiryDate(request.getExpiryDate());

        return toResponse(inventoryRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory", "id", id);
        }
        inventoryRepository.deleteById(id);
    }

    private InventoryResponse toResponse(Inventory i) {
        return InventoryResponse.builder()
                .id(i.getId())
                .itemName(i.getItemName())
                .category(i.getCategory())
                .quantity(i.getQuantity())
                .unit(i.getUnit())
                .locationName(i.getLocation() != null ? i.getLocation().getName() : null)
                .disasterTitle(i.getDisaster() != null ? i.getDisaster().getTitle() : null)
                .minThreshold(i.getMinThreshold())
                .lowStock(i.isLowStock())
                .expiryDate(i.getExpiryDate())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
