package com.beyond.sportsmatch.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank
        String id,   // 로그인 ID 또는 이메일
        @NotBlank
        String password
) {}