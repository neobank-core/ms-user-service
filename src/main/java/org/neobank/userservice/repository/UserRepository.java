package org.neobank.userservice.repository;

import org.neobank.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakUserId(String keycloakUserId);
}
