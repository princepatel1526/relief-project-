package com.disasterrelief.controller;

import com.disasterrelief.dto.request.ChangePasswordRequest;
import com.disasterrelief.dto.request.ProfileUpdateRequest;
import com.disasterrelief.dto.response.UserDto;
import com.disasterrelief.entity.User;
import com.disasterrelief.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Admin: list all users ─────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    // ── Current user: get own profile ─────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(Authentication authentication) {
        User user = findCurrentUser(authentication);
        return ResponseEntity.ok(toDto(user));
    }

    // ── Current user: update own profile ─────────────────────────────
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMe(@Valid @RequestBody ProfileUpdateRequest req,
                                             Authentication authentication) {
        User user = findCurrentUser(authentication);

        // Check email uniqueness if changing
        if (!user.getEmail().equalsIgnoreCase(req.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email is already in use by another account");
            }
        }

        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        userRepository.save(user);

        return ResponseEntity.ok(toDto(user));
    }

    // ── Current user: change password ─────────────────────────────────
    @PostMapping("/me/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            Authentication authentication) {

        User user = findCurrentUser(authentication);

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Current password is incorrect");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private User findCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found"));
    }

    private UserDto toDto(User user) {
        if (user == null) return UserDto.builder().build();

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .toList();

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roles(roles)
                .build();
    }
}
