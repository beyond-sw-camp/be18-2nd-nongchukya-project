package com.springboot.jwttest.jwt.auth.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        long   expiresInSec
) {}
