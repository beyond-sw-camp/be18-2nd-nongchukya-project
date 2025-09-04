package com.beyond.match.jwt.auth.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        long   expiresInSec
) {}