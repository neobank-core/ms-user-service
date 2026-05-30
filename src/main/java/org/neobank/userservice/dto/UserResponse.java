package org.neobank.userservice.dto;

import lombok.*;

public record UserResponse(Long id, String username, String email, String firstName, String lastName, boolean isBlocked) {
}
