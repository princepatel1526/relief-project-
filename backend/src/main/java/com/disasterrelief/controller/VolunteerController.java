package com.disasterrelief.controller;

import com.disasterrelief.dto.request.VolunteerRequest;
import com.disasterrelief.dto.response.VolunteerResponse;
import com.disasterrelief.entity.Volunteer;
import com.disasterrelief.service.impl.VolunteerServiceImpl;
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
import java.util.Map;

@RestController
@RequestMapping("/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerServiceImpl volunteerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    public ResponseEntity<Page<VolunteerResponse>> getAllVolunteers(
            @RequestParam(required = false) Volunteer.Availability availability,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(volunteerService.getAllVolunteers(availability, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    public ResponseEntity<VolunteerResponse> getVolunteer(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getVolunteerById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<VolunteerResponse> registerVolunteer(@Valid @RequestBody VolunteerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(volunteerService.registerVolunteer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerResponse> updateVolunteer(@PathVariable Long id,
                                                               @Valid @RequestBody VolunteerRequest request) {
        return ResponseEntity.ok(volunteerService.updateVolunteer(id, request));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<VolunteerResponse> updateAvailability(@PathVariable Long id,
                                                                  @RequestBody Map<String, String> body) {
        Volunteer.Availability availability = Volunteer.Availability.valueOf(body.get("availability"));
        return ResponseEntity.ok(volunteerService.updateAvailability(id, availability));
    }

    @GetMapping("/nearby")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    public ResponseEntity<List<VolunteerResponse>> getNearbyVolunteers(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false) String skill,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(volunteerService.findNearby(lat, lng, skill, limit));
    }
}
