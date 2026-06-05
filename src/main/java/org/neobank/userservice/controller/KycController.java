package org.neobank.userservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.neobank.userservice.dto.KycResponse;
import org.neobank.userservice.dto.KycSubmissionRequest;
import org.neobank.userservice.entity.User;
import org.neobank.userservice.entity.UserKyc;
import org.neobank.userservice.mapper.KycMapper;
import org.neobank.userservice.service.KycService;
import org.neobank.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/me/kyc")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class KycController {

    private final UserService userService;
    private final KycService kycService;
    private final KycMapper kycMapper;

    @PostMapping
    public ResponseEntity<KycResponse> submitKyc(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody KycSubmissionRequest request) {
        User user = userService.getOrCreateUser(jwt);
        UserKyc userKyc = kycService.submitKyc(user, request.documentType(), request.documentNumber());
        return ResponseEntity.ok(kycMapper.toResponse(userKyc));
    }
}
