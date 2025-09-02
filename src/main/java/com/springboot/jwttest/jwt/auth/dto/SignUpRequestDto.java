package com.springboot.jwttest.jwt.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequestDto(
        @NotBlank String id,           // 로그인 ID
        @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String nickname
//        @NotBlank String gender,
//        @NotBlank boolean dm_option

) {


}
