package org.neobank.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.neobank.userservice.dto.UpdateUserRequest;
import org.neobank.userservice.dto.UserResponse;
import org.neobank.userservice.entity.User;
import org.neobank.userservice.mapper.UserMapper;
import org.neobank.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getOrCreateUser(jwt);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT') or hasRole('KYC_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserResponse> response = users.map(userMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateCurrentUser(jwt, request);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    @PostMapping("/{id}/block")
    public ResponseEntity<UserResponse> blockUser(@PathVariable Long id) {
        User user = userService.blockUser(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    @PostMapping("/{id}/unblock")
    public ResponseEntity<UserResponse> unblockUser(@PathVariable Long id) {
        User user = userService.unblockUser(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
