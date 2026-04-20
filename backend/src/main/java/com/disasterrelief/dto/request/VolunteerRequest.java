package com.disasterrelief.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VolunteerRequest {
    @NotBlank
    private String skills;

    private String languages;
    private Integer experienceYears = 0;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    private String address;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    private String emergencyContact;
}
