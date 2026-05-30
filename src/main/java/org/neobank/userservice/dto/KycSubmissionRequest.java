package org.neobank.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record KycSubmissionRequest(@NotBlank(message = "Document type is required") String documentType) {
}
