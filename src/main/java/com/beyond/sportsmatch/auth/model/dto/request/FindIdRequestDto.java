package com.beyond.sportsmatch.auth.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindIdRequestDto {
    // 아이디 찾기

    @NotBlank
    @Email
    private String email;
}
