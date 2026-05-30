package org.neobank.userservice.service;

import lombok.RequiredArgsConstructor;
import org.neobank.userservice.dto.RegisterUserRequest;
import org.neobank.userservice.dto.UpdateUserRequest;
import org.neobank.userservice.entity.User;
import org.neobank.userservice.event.UserRegisteredEvent;
import org.neobank.userservice.exception.UserNotFoundException;
import org.neobank.userservice.publisher.UserEventPublisher;
import org.neobank.userservice.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;
    private final UserEventPublisher userEventPublisher;

    public User getUserByKeycloakId(String keycloakUserId) {
        return userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with Keycloak ID: " + keycloakUserId));
    }

    @Cacheable(value = "user-profile", key = "#jwt.subject")
    public User getOrCreateUser(Jwt jwt) {
        String keycloakUserId = jwt.getSubject();

        return userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseGet(() -> createUserFromJwt(jwt));
    }

    private User createUserFromJwt(Jwt jwt) {
        User user = User.builder()
                .keycloakUserId(jwt.getSubject())
                .username(jwt.getClaim("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @CacheEvict(value = "user-profile", key = "#jwt.subject")
    public User updateCurrentUser(Jwt jwt, UpdateUserRequest request) {
        User user = getUserByKeycloakId(jwt.getSubject());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return userRepository.save(user);
    }

    public User createUser(RegisterUserRequest request) {
        String keycloakUserId = keycloakUserService.createUser(request);

        User user = User.builder()
                .username(request.username())
                .keycloakUserId(keycloakUserId)
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone()).build();

        User savedUser = userRepository.save(user);
        UserRegisteredEvent event =
                new UserRegisteredEvent(
                        savedUser.getId(),
                        savedUser.getUsername(),
                        savedUser.getKeycloakUserId(),
                        savedUser.getEmail(),
                        savedUser.getFirstName(),
                        savedUser.getLastName(),
                        savedUser.getPhone(),
                        savedUser.getCreatedAt()
                );
        userEventPublisher.publishUserRegistered(event);

        return savedUser;
    }
}
