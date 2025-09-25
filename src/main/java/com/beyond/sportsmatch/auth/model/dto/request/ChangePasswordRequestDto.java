package com.beyond.sportsmatch.auth.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 비밀번호 변경
public class ChangePasswordRequestDto {
    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword; // 새 비밀번호
}