package org.neobank.userservice.dto;

import org.neobank.userservice.enums.KycStatus;

import java.time.LocalDateTime;

public record KycResponse(Long id, KycStatus status, String documentType, LocalDateTime submittedAt) {
}
