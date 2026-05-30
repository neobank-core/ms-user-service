package org.neobank.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.neobank.userservice.dto.LoginRequest;
import org.neobank.userservice.dto.LoginResponse;
import org.neobank.userservice.dto.RegisterUserRequest;
import org.neobank.userservice.dto.UserResponse;
import org.neobank.userservice.entity.User;
import org.neobank.userservice.mapper.UserMapper;
import org.neobank.userservice.service.AuthService;
import org.neobank.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
