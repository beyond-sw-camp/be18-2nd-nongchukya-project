package com.beyond.sportsmatch.domain.mypage.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDto {
    private String targetUserNickname;  // 신고 대상자 ID
    private String reason;
    private String description;
    private String evidenceUrl; // 첨부파일 URL
}

