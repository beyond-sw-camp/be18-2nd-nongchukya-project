package com.beyond.sportsmatch.auth.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        long   expiresInSec
) {}