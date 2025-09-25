package com.beyond.sportsmatch.domain.mypage.model.service;

import com.beyond.sportsmatch.domain.mypage.model.entity.Report;
import com.beyond.sportsmatch.domain.mypage.model.repository.ReportRepository;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public Report reportUser(User reporter, User targetUser, String reason, String description) {
        // 자기 자신 신고 방지
        if (Objects.equals(reporter.getUserId(), targetUser.getUserId())) {
            throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setTargetUser(targetUser);
        report.setReason(reason);
        report.setDescription(description);



        return reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getMyReports(User reporter) {
        return reportRepository.findByReporter(reporter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getReportsAgainstMe(User targetUser) {
        return reportRepository.findByTargetUser(targetUser);
    }
}
