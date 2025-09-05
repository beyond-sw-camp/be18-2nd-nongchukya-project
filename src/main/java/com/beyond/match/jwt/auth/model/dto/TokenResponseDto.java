package com.beyond.match.jwt.auth.model.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        long   expiresInSec
) {}