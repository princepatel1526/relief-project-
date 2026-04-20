package com.disasterrelief.dto.response;

import com.disasterrelief.entity.Volunteer;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class VolunteerResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String skills;
    private String languages;
    private Integer experienceYears;
    private Volunteer.Availability availability;
    private Double latitude;
    private Double longitude;
    private String address;
    private Boolean isVerified;
    private Double totalHours;
    private Double rating;
    private Double distanceKm;
}
