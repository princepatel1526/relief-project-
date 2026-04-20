package com.disasterrelief.controller;

import com.disasterrelief.dto.request.AssignmentRequest;
import com.disasterrelief.dto.response.AssignmentResponse;
import com.disasterrelief.entity.Assignment;
import com.disasterrelief.service.impl.AssignmentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentServiceImpl assignmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<AssignmentResponse> createAssignment(@Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(request));
    }

    @GetMapping("/disaster/{disasterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<Page<AssignmentResponse>> getByDisaster(
            @PathVariable Long disasterId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByDisaster(disasterId, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AssignmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Assignment.AssignmentStatus status = Assignment.AssignmentStatus.valueOf((String) body.get("status"));
        Double hours = body.containsKey("hoursLogged") ? Double.parseDouble(body.get("hoursLogged").toString()) : null;
        return ResponseEntity.ok(assignmentService.updateStatus(id, status, hours));
    }
}
