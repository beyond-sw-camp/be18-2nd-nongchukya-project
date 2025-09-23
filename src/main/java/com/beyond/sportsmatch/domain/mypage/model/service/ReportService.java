package com.beyond.sportsmatch.domain.mypage.model.service;



import com.beyond.sportsmatch.domain.mypage.model.entity.Report;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {

    // 1) 신고 등록 (첨부파일 포함)
    Report reportUser(User reporter, User targetUser, String reason, String description);
    // 2) 내가 한 신고 조회
    List<Report> getMyReports(User reporter);

    // 3) 내가 받은 신고 조회
    List<Report> getReportsAgainstMe(User targetUser);
}