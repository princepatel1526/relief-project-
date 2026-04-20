package com.disasterrelief.controller;

import com.disasterrelief.dto.request.DisasterRequest;
import com.disasterrelief.dto.response.DisasterResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.service.impl.DisasterServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disasters")
@RequiredArgsConstructor
public class DisasterController {

    private final DisasterServiceImpl disasterService;

    @GetMapping
    public ResponseEntity<Page<DisasterResponse>> getAllDisasters(
            @RequestParam(required = false) Disaster.DisasterStatus status,
            @RequestParam(required = false) Disaster.Severity severity,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(disasterService.getAllDisasters(status, severity, pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<DisasterResponse>> getMyDisasters(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(disasterService.getMyDisasters(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisasterResponse> getDisaster(@PathVariable Long id) {
        return ResponseEntity.ok(disasterService.getDisasterById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<DisasterResponse> createDisaster(@Valid @RequestBody DisasterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(disasterService.createDisaster(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<DisasterResponse> updateDisaster(@PathVariable Long id,
                                                             @Valid @RequestBody DisasterRequest request) {
        return ResponseEntity.ok(disasterService.updateDisaster(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<DisasterResponse> updateStatus(@PathVariable Long id,
                                                          @RequestBody Map<String, String> body) {
        Disaster.DisasterStatus status = Disaster.DisasterStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(disasterService.updateStatus(id, status));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<DisasterResponse>> getNearbyDisasters(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "50") double radiusKm) {
        return ResponseEntity.ok(disasterService.getNearbyDisasters(lat, lng, radiusKm));
    }
}
