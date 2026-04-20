package com.disasterrelief.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;

    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

    public String principal() {
        if (email != null && !email.isBlank()) return email;
        return usernameOrEmail;
    }
}
