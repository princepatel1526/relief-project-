package com.disasterrelief.config;

import com.disasterrelief.entity.Role;
import com.disasterrelief.entity.User;
import com.disasterrelief.repository.RoleRepository;
import com.disasterrelief.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedAdminUser() {
        return args -> {
            Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(Role.RoleName.ROLE_ADMIN)
                            .description("System Administrator")
                            .build()));

            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                String encoded = passwordEncoder.encode("password");
                User admin = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(encoded)
                        .fullName("System Administrator")
                        .isActive(true)
                        .roles(Set.of(adminRole))
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
