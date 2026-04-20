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
    @Column(nullable = false, unique = true)
    private RoleName name;

    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }

    public enum RoleName {
        ROLE_ADMIN, ROLE_COORDINATOR, ROLE_VOLUNTEER, ROLE_DONOR,
        ROLE_CITIZEN, ROLE_RESPONDER, ROLE_NGO, ROLE_SUPER_ADMIN
    }
}
