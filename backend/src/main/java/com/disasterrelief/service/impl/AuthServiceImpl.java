package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.LoginRequest;
import com.disasterrelief.dto.request.RegisterRequest;
import com.disasterrelief.dto.response.AuthResponse;
import com.disasterrelief.entity.Role;
import com.disasterrelief.entity.User;
import com.disasterrelief.exception.BusinessException;
import com.disasterrelief.repository.RoleRepository;
import com.disasterrelief.repository.UserRepository;
import com.disasterrelief.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String principal = request.principal();
        if (principal == null || principal.isBlank()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        User existingUser = userRepository.findByEmail(principal)
                .or(() -> userRepository.findByUsername(principal))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String storedPassword = existingUser.getPassword();
        if (storedPassword == null || !passwordEncoder.matches(request.getPassword(), storedPassword)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(existingUser.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        String accessToken = jwtTokenProvider.generateToken(auth);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());

        User user = userRepository.findByUsername(
                ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())
                .orElseThrow();

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        log.info("User '{}' logged in successfully", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("USERNAME_TAKEN", "Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_TAKEN", "Email '" + request.getEmail() + "' is already registered");
        }

        Role.RoleName roleName = resolveRole(request.getRole());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessException("Role not found: " + roleName));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .roles(Set.of(role))
                .isActive(true)
                .build();

        userRepository.save(user);
        log.info("Registered new user: {} with role {}", user.getUsername(), roleName);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(request.getEmail());
        loginRequest.setPassword(request.getPassword());
        return login(loginRequest);
    }

    private Role.RoleName resolveRole(String roleInput) {
        if (roleInput == null || roleInput.isBlank()) return Role.RoleName.ROLE_CITIZEN;
        return switch (roleInput.toUpperCase().replace("ROLE_", "")) {
            case "ADMIN"       -> Role.RoleName.ROLE_ADMIN;
            case "SUPER_ADMIN" -> Role.RoleName.ROLE_SUPER_ADMIN;
            case "COORDINATOR" -> Role.RoleName.ROLE_COORDINATOR;
            case "DONOR"       -> Role.RoleName.ROLE_DONOR;
            case "RESPONDER"   -> Role.RoleName.ROLE_RESPONDER;
            case "NGO_COORDINATOR", "NGO-COORDINATOR" -> Role.RoleName.ROLE_NGO_COORDINATOR;
            case "NGO"         -> Role.RoleName.ROLE_NGO;
            case "CITIZEN"     -> Role.RoleName.ROLE_CITIZEN;
            default            -> Role.RoleName.ROLE_VOLUNTEER;
        };
    }
}
