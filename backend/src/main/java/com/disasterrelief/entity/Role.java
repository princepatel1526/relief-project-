package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 80, columnDefinition = "VARCHAR(80)")
    private RoleName name;

    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }

    public enum RoleName {
        ROLE_CITIZEN,
        ROLE_VOLUNTEER,
        ROLE_RESPONDER,
        ROLE_NGO_COORDINATOR,
        ROLE_ADMIN,
        ROLE_SUPER_ADMIN
    }
}
