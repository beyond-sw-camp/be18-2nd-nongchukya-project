package com.beyond.sportsmatch.domain.mypage.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ReportRequestDto {
    private String targetUserNickname;  // 신고 대상자 닉네임
    private String reason;               // 신고 사유
    private String description;          // 상세 설명

}
