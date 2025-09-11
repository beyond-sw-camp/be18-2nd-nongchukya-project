package com.beyond.sportsmatch.domain.mypage.model.service;



import com.beyond.sportsmatch.domain.mypage.model.entity.Report;
import com.beyond.sportsmatch.domain.user.model.entity.User;

import java.util.List;

public interface ReportService {

    // 1) 신고 등록
    Report reportUser(User reporter, User targetUser, String reason, String description, String evidenceUrl);

    // 2) 내가 한 신고 조회
    List<Report> getMyReports(User reporter);

    // 3) 내가 받은 신고 조회
    List<Report> getReportsAgainstMe(User targetUser);
}