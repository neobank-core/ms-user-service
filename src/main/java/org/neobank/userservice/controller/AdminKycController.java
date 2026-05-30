package org.neobank.userservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.neobank.userservice.dto.KycResponse;
import org.neobank.userservice.entity.UserKyc;
import org.neobank.userservice.mapper.KycMapper;
import org.neobank.userservice.service.KycService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/kyc")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminKycController {

    private final KycService kycService;
    private final KycMapper kycMapper;

    @PreAuthorize("hasRole('ADMIN') or hasRole('KYC_MANAGER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<KycResponse> approveKyc(@PathVariable Long id) {
        UserKyc userKyc = kycService.approveKyc(id);
        return ResponseEntity.ok(kycMapper.toResponse(userKyc));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('KYC_MANAGER')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<KycResponse> rejectKyc(@PathVariable Long id) {
        UserKyc userKyc = kycService.rejectKyc(id);
        return ResponseEntity.ok(kycMapper.toResponse(userKyc));
    }
}
