package org.neobank.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(name = "keycloak_user_id", unique = true, nullable = false)
    private String keycloakUserId;

    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    private LocalDateTime createdAt;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
