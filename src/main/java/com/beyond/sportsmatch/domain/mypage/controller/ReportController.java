package com.beyond.sportsmatch.domain.mypage.controller;


import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.dto.BaseResponseDto;
import com.beyond.sportsmatch.domain.mypage.model.dto.ReportRequestDto;
import com.beyond.sportsmatch.domain.mypage.model.dto.ReportResponseDto;
import com.beyond.sportsmatch.domain.mypage.model.entity.Report;
import com.beyond.sportsmatch.domain.mypage.model.service.ReportService;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/mypage/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;
    // 1) 신고 등록 (로그인 유저 기준)
    @PostMapping
    public BaseResponseDto<ReportResponseDto> createReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails, // JWT에서 추출한 로그인 유저
            @RequestBody ReportRequestDto request
    ) {
        User loginUser = userDetails.getUser(); // 실제 User 객체
//        User targetUser = new User();
//        targetUser.setUserId(request.getTargetUserId());
        // 1) 닉네임으로 신고 대상자 조회
        User targetUser = userRepository.findByNickname(request.getTargetUserNickname())
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임 사용자 없음"));
        Report report = reportService.reportUser(
                loginUser,
                targetUser,
                request.getReason(),
                request.getDescription(),
                request.getEvidenceUrl()
        );

        return new BaseResponseDto<>(HttpStatus.CREATED, ReportResponseDto.from(report));
    }

    // 2) 내가 한 신고 조회
    @GetMapping("/submitted")
    public BaseResponseDto<ReportResponseDto> getMyReports(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User loginUser = userDetails.getUser();
        List<ReportResponseDto> reports = reportService.getMyReports(loginUser)
                .stream()
                .map(ReportResponseDto::from)
                .collect(Collectors.toList());
        return new BaseResponseDto<>(HttpStatus.OK, reports);
    }

    // 3) 내가 받은 신고 조회
    @GetMapping("/received")
    public BaseResponseDto<ReportResponseDto> getReportsAgainstMe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User loginUser = userDetails.getUser();
        List<ReportResponseDto> reports = reportService.getReportsAgainstMe(loginUser)
                .stream()
                .map(ReportResponseDto::from)
                .collect(Collectors.toList());
        return new BaseResponseDto<>(HttpStatus.OK, reports);
    }
}