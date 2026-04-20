package com.disasterrelief.controller;

import com.disasterrelief.dto.request.ReliefRequestDto;
import com.disasterrelief.dto.response.ReliefRequestResponse;
import com.disasterrelief.entity.ReliefRequest;
import com.disasterrelief.service.impl.ReliefRequestServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ReliefRequestController {

    private final ReliefRequestServiceImpl requestService;

    @GetMapping
    public ResponseEntity<Page<ReliefRequestResponse>> getAllRequests(
            @RequestParam(required = false) Long disasterId,
            @RequestParam(required = false) ReliefRequest.RequestStatus status,
            @PageableDefault(size = 20, sort = "urgencyLevel", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(requestService.getAllRequests(disasterId, status, pageable));
    }

    @GetMapping("/priority")
    public ResponseEntity<List<ReliefRequestResponse>> getPriorityRequests(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(requestService.getPendingByPriority(limit));
    }

    @PostMapping
    public ResponseEntity<ReliefRequestResponse> createRequest(@Valid @RequestBody ReliefRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReliefRequestResponse> updateStatus(@PathVariable Long id,
                                                               @RequestBody Map<String, String> body) {
        ReliefRequest.RequestStatus status = ReliefRequest.RequestStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(requestService.updateStatus(id, status));
    }
}
