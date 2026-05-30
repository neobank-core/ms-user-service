package org.neobank.userservice.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Long refreshExpiresIn,
        String tokenType
) {
}
