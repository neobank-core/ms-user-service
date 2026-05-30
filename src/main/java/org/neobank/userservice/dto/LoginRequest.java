package org.neobank.userservice.dto;

public record LoginRequest(
        String username,
        String password
) {
}
