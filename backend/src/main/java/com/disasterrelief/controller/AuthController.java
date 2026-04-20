package com.disasterrelief.controller;

import com.disasterrelief.dto.request.LoginRequest;
import com.disasterrelief.dto.request.RegisterRequest;
import com.disasterrelief.dto.request.ResetPasswordRequest;
import com.disasterrelief.dto.response.AuthResponse;
import com.disasterrelief.entity.User;
import com.disasterrelief.repository.UserRepository;
import com.disasterrelief.service.OtpService;
import com.disasterrelief.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService      otpService;

    // ── Standard auth ─────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // ── OTP: send ─────────────────────────────────────────────────────────────
    /**
     * POST /api/auth/send-otp
     * Body: { "phone": "9876543210" }
     *
     * Generates a 6-digit OTP and stores it for 5 minutes.
     * NOTE: No SMS provider is wired in this build, so the raw OTP is returned
     * in the "devOtp" field for testing. Remove that field when integrating a
     * real SMS gateway (Twilio, MSG91, etc.).
     */
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> body) {
        String phone = body.getOrDefault("phone", "").trim();
        if (phone.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phone number is required");
        }
        if (!phone.matches("[6-9][0-9]{9}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a valid 10-digit Indian mobile number");
        }

        // Ensure an account exists for this phone before sending OTP
        userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No account is linked to this phone number"));

        String otp = otpService.generate(phone);

        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to your registered mobile number",
                // ── DEV ONLY — remove this field in production ──
                "devOtp", otp
        ));
    }

    // ── OTP: forgot username ──────────────────────────────────────────────────
    /**
     * POST /api/auth/forgot-username
     * Body: { "phone": "9876543210", "otp": "123456" }
     *
     * Returns the full username and email after successful OTP verification.
     */
    @PostMapping("/forgot-username")
    public ResponseEntity<Map<String, String>> forgotUsername(
            @RequestBody Map<String, String> body) {

        String phone = body.getOrDefault("phone", "").trim();
        String otp   = body.getOrDefault("otp",   "").trim();

        if (phone.isBlank() || otp.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phone number and OTP are required");
        }

        if (!otpService.verify(phone, otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid or expired OTP. Please request a new one.");
        }

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No account found with that phone number"));

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email",    user.getEmail()
        ));
    }

    // ── OTP: reset password ───────────────────────────────────────────────────
    /**
     * POST /api/auth/reset-password
     * Body: { "phone": "9876543210", "otp": "123456",
     *          "newPassword": "...", "confirmPassword": "..." }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Passwords do not match");
        }

        if (!otpService.verify(req.getPhone(), req.getOtp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid or expired OTP. Please request a new one.");
        }

        User user = userRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No account found with that phone number"));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message",
                "Password reset successfully. You can now sign in with your new password."));
    }
}
