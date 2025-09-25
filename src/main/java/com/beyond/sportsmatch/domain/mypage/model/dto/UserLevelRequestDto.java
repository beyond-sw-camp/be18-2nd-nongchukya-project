package com.beyond.sportsmatch.domain.mypage.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLevelRequestDto {
    private String sportName;    // 수정할 운동 이름
    private Integer levelId;     // 선택할 레벨 PK
    private Boolean interest;    // 관심 운동 여부
}