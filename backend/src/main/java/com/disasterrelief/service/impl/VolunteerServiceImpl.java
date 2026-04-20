package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.VolunteerRequest;
import com.disasterrelief.dto.response.VolunteerResponse;
import com.disasterrelief.entity.User;
import com.disasterrelief.entity.Volunteer;
import com.disasterrelief.exception.BusinessException;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.UserRepository;
import com.disasterrelief.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerServiceImpl {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<VolunteerResponse> getAllVolunteers(Volunteer.Availability availability, Pageable pageable) {
        Page<Volunteer> volunteers = availability != null
                ? volunteerRepository.findByAvailability(availability, pageable)
                : volunteerRepository.findAll(pageable);
        return volunteers.map(v -> toResponse(v, null));
    }

    @Transactional(readOnly = true)
    public VolunteerResponse getVolunteerById(Long id) {
        return toResponse(findById(id), null);
    }

    @Transactional(readOnly = true)
    public VolunteerResponse getMyVolunteer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Volunteer volunteer = volunteerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "userId", user.getId()));
        return toResponse(volunteer, null);
    }

    @Transactional(readOnly = true)
    public List<VolunteerResponse> findNearby(double lat, double lng, String skill, int limit) {
        List<Volunteer> volunteers = volunteerRepository.findAvailableVolunteersNearby(
                lat, lng, skill, PageRequest.of(0, limit));
        return volunteers.stream()
                .map(v -> toResponse(v, haversineKm(lat, lng, v.getLatitude(), v.getLongitude())))
                .toList();
    }

    @Transactional
    public VolunteerResponse registerVolunteer(VolunteerRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (volunteerRepository.findByUserId(user.getId()).isPresent()) {
            throw new BusinessException("ALREADY_REGISTERED", "You are already registered as a volunteer");
        }

        Volunteer volunteer = Volunteer.builder()
                .user(user)
                .skills(request.getSkills())
                .languages(request.getLanguages())
                .experienceYears(request.getExperienceYears())
                .availability(Volunteer.Availability.AVAILABLE)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .emergencyContact(request.getEmergencyContact())
                .isVerified(false)
                .build();

        return toResponse(volunteerRepository.save(volunteer), null);
    }

    @Transactional
    public VolunteerResponse updateAvailability(Long id, Volunteer.Availability availability) {
        Volunteer volunteer = findById(id);
        volunteer.setAvailability(availability);
        log.info("Volunteer {} availability updated to {}", id, availability);
        return toResponse(volunteerRepository.save(volunteer), null);
    }

    @Transactional
    public VolunteerResponse updateMyAvailability(Volunteer.Availability availability) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Volunteer volunteer = volunteerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "userId", user.getId()));
        volunteer.setAvailability(availability);
        return toResponse(volunteerRepository.save(volunteer), null);
    }

    @Transactional
    public VolunteerResponse updateVolunteer(Long id, VolunteerRequest request) {
        Volunteer v = findById(id);
        v.setSkills(request.getSkills());
        v.setLanguages(request.getLanguages());
        v.setExperienceYears(request.getExperienceYears());
        v.setLatitude(request.getLatitude());
        v.setLongitude(request.getLongitude());
        v.setAddress(request.getAddress());
        v.setEmergencyContact(request.getEmergencyContact());
        return toResponse(volunteerRepository.save(v), null);
    }

    private Volunteer findById(Long id) {
        return volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", "id", id));
    }

    private double haversineKm(double lat1, double lng1, Double lat2, Double lng2) {
        if (lat2 == null || lng2 == null) return -1;
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private VolunteerResponse toResponse(Volunteer v, Double distanceKm) {
        return VolunteerResponse.builder()
                .id(v.getId())
                .userId(v.getUser().getId())
                .fullName(v.getUser().getFullName())
                .email(v.getUser().getEmail())
                .phone(v.getUser().getPhone())
                .skills(v.getSkills())
                .languages(v.getLanguages())
                .experienceYears(v.getExperienceYears())
                .availability(v.getAvailability())
                .latitude(v.getLatitude())
                .longitude(v.getLongitude())
                .address(v.getAddress())
                .isVerified(v.getIsVerified())
                .totalHours(v.getTotalHours())
                .rating(v.getRating())
                .distanceKm(distanceKm)
                .build();
    }
}
