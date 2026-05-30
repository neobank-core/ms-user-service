package org.neobank.userservice.repository;

import org.neobank.userservice.entity.User;
import org.neobank.userservice.entity.UserKyc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserKycRepository extends JpaRepository<UserKyc, Long> {
    Optional<UserKyc> findByUser(User user);
}
