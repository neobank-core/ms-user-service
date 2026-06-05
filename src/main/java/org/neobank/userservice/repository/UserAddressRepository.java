package org.neobank.userservice.repository;

import org.neobank.userservice.entity.UserAddress;
import org.neobank.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    Optional<UserAddress> findByUser(User user);
}
