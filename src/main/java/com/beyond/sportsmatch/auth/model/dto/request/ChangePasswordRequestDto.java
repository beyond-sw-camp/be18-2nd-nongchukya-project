package com.beyond.sportsmatch.auth.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 비밀번호 변경
public class ChangePasswordRequestDto {
    private String password; // 새 비밀번호
}