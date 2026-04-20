package com.disasterrelief.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // --- Token generation ---

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return buildToken(userDetails.getUsername(), jwtExpirationMs, roles);
    }

    public String generateTokenFromUsername(String username) {
        // No roles embedded — caller must use generateToken(Authentication) for role-bearing tokens
        return buildToken(username, jwtExpirationMs, List.of());
    }

    public String generateRefreshToken(String username) {
        // Refresh tokens are intentionally role-free
        return buildToken(username, refreshExpirationMs, List.of());
    }

    private String buildToken(String subject, long expirationMs, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry);
        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }
        return builder.signWith(key()).compact();
    }

    // --- Token parsing ---

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts the "roles" claim from the token.
     * Uses raw Object extraction + instanceof check to survive any JJWT/Jackson
     * deserialization quirk (e.g. LinkedHashMap, ArrayList subtype, etc.).
     * Always returns a non-null list.
     */
    public List<String> getRolesFromToken(String token) {
        try {
            Object rolesObj = parseClaims(token).get("roles");
            if (rolesObj instanceof List<?> rawList) {
                return rawList.stream()
                        .filter(java.util.Objects::nonNull)
                        .map(Object::toString)
                        .toList();
            }
            log.warn("[JWT] 'roles' claim is missing or not a List — type was: {}",
                    rolesObj == null ? "null" : rolesObj.getClass().getName());
            return List.of();
        } catch (Exception e) {
            log.warn("[JWT] Could not extract roles from token: {}", e.getMessage());
            return List.of();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
