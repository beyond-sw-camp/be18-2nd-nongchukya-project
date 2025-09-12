package com.beyond.sportsmatch.auth.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindIdResponseDto {
    private String userId; // 마스킹된 로그인 아이디
}
