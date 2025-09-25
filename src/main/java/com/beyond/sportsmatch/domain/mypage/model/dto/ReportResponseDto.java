package com.beyond.sportsmatch.domain.mypage.model.dto;


import com.beyond.sportsmatch.domain.mypage.model.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReportResponseDto {
    private int reportId;
    private int reporterId;
    private String targetUserNickname;
    private String reason;
    private String description;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static ReportResponseDto from(Report report) {
        return new ReportResponseDto(
                report.getReportId(),
                report.getReporter().getUserId(),
                report.getTargetUser().getNickname(),
                report.getReason(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt(),  // 추가
                report.getUpdatedAt()   // 추가
        );
    }
}