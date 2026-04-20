package com.disasterrelief.config;

import com.disasterrelief.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Static frontend ───────────────────────────────────────────
                .requestMatchers("/", "/*.html", "/css/**", "/js/**",
                                 "/assets/**", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ── Public API endpoints ──────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()  // covers login, register, forgot-username, reset-password
                .requestMatchers("/api/payments/webhook").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/disasters/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/disaster-types/**").permitAll()
                .requestMatchers("/api/actuator/health", "/actuator/health").permitAll()

                // ── Current-user profile (any authenticated role) ─────────────
                .requestMatchers("/api/users/me", "/api/users/me/**").authenticated()

                // ── Role-protected API endpoints ──────────────────────────────
                .requestMatchers("/api/users", "/api/users/**")
                    .hasAnyRole("ADMIN", "COORDINATOR")
                .requestMatchers("/api/admin/**")
                    .hasAnyRole("ADMIN", "COORDINATOR")
                // Citizens can submit relief requests and view disasters
                .requestMatchers(HttpMethod.POST, "/api/requests")
                    .hasAnyRole("ADMIN", "COORDINATOR", "VOLUNTEER", "CITIZEN", "RESPONDER", "NGO")
                // Volunteers, Coordinators, Admins, Responders, NGOs can report/update disasters — Donors/Citizens cannot
                .requestMatchers(HttpMethod.POST,   "/api/disasters/**")
                    .hasAnyRole("ADMIN", "COORDINATOR", "VOLUNTEER", "RESPONDER", "NGO")
                .requestMatchers(HttpMethod.PUT,    "/api/disasters/**")
                    .hasAnyRole("ADMIN", "COORDINATOR", "VOLUNTEER", "RESPONDER", "NGO")
                .requestMatchers(HttpMethod.PATCH,  "/api/disasters/**")
                    .hasAnyRole("ADMIN", "COORDINATOR", "VOLUNTEER", "RESPONDER", "NGO")
                .requestMatchers(HttpMethod.DELETE, "/api/disasters/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/assignments/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR")
                // Volunteers, Coordinators, Admins, Responders can manage inventory — Donors/Citizens cannot
                .requestMatchers("/api/inventory/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR", "VOLUNTEER", "RESPONDER")
                // Analytics — coordinators and above
                .requestMatchers("/api/admin/analytics")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR", "NGO")

                // ── Everything else requires login ────────────────────────────
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> originPatterns = new ArrayList<>(
                Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList());
        originPatterns.add("null");
        config.setAllowedOriginPatterns(originPatterns);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
