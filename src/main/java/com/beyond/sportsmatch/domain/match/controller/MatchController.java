package com.beyond.sportsmatch.domain.match.controller;


import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.dto.ItemsResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.service.MatchService;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/match-service")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    // 매칭 신청
    @PostMapping("/match-applications")
    public ResponseEntity<String> createMatch(@RequestBody MatchRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 로그인된 유저 정보 가져오기
        User user = userDetails.getUser();

        matchService.saveMatch(requestDto, user);

        return ResponseEntity.status(HttpStatus.OK).body("매칭 신청이 성공적으로 완료되었습니다.");
    }

    // 매칭 조회
    @GetMapping("/match-applications/{applicationId}")
    public ResponseEntity<MatchApplicationResponseDto> getMatch(@PathVariable("applicationId") int applicationId) {

        MatchApplication matchApplication = matchService.getMatch(applicationId);

        return ResponseEntity.status(HttpStatus.OK).body(new MatchApplicationResponseDto(matchApplication));
    }

    // 매칭 신청 취소(매칭 신청 삭제)
    @DeleteMapping("/match-applications/{applicationId}")
    public ResponseEntity<String> deleteMatch(@PathVariable("applicationId") int applicationId) {

        matchService.deleteMatch(applicationId);

        return ResponseEntity.status(HttpStatus.OK).body("Match has been deleted.");
    }

    // 매칭 신청 리스트 조회
//    @GetMapping("/match-applications")
//    public ResponseEntity<ItemsResponseDto<MatchApplicationResponseDto>> getMatchApplications(@RequestParam int page,
//                                                                                    @RequestParam int numOfRows) {
//
//        int totalCount = matchService.getTotalCount();
//        List<MatchApplicationResponseDto> matches = matchService.getMatchApplications(page, numOfRows);
//
//        return ResponseEntity.ok(
//                new ItemsResponseDto<>(HttpStatus.OK, matches, page, totalCount));
//    }

    // 매칭 신청 리스트 조회 (사용자 본인것만)
    @GetMapping("/match-applications")
    public ResponseEntity<ItemsResponseDto<MatchApplicationResponseDto>> getMatchApplications(@RequestParam int page,
                                                                                              @RequestParam int numOfRows,
                                                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User applicant = userDetails.getUser();

        int totalCount = matchService.getTotalCountForUser(applicant);
        List<MatchApplicationResponseDto> matches = matchService.getMatchApplications(page, numOfRows, applicant);

        return ResponseEntity.ok(
                new ItemsResponseDto<>(HttpStatus.OK, matches, page, totalCount));
    }

    // 매칭 중인 리스트 조회 (전체)
//    @GetMapping("/matches")
//    public ResponseEntity<ItemsResponseDto<MatchResponseDto>> getMatches(@RequestParam int page,
//                                                                         @RequestParam int numOfRows) {
//        int totalCount = matchService.getTotalCount();
//        List<MatchResponseDto> matches = matchService.getMatches(page, numOfRows);
//
//        return ResponseEntity.ok(
//                new ItemsResponseDto<>(HttpStatus.OK, matches, page, totalCount));
//    }

    // 매칭 중인 리스트 조회 (사용자 본인것만)
    @GetMapping("/my-matches")
    public ResponseEntity<Page<MatchResponseDto>> getMatchingList(@PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Page<MatchResponseDto> matches = matchService.getMatchesByUser(user, pageable);

        System.out.println(matches);

        return ResponseEntity.ok(matches);
    }

    // 마감 임박 매칭 리스트 조회 (1-2명)
    @GetMapping("/imminent-matches")
    public ResponseEntity<Set<String>> getImminentMatches() {
        Set<String> imminentMatches = matchService.getImminentMatches();

        return ResponseEntity.status(HttpStatus.OK).body(imminentMatches);
    }

    // 매칭 완료 리스트 조회
    @GetMapping("/completed-matches")
    public ResponseEntity<List<MatchCompleted>> getCompletedMatches() {
        List<MatchCompleted> completedMatches = matchService.getCompletedMatches();

        return ResponseEntity.status(HttpStatus.OK).body(completedMatches);
    }

    // 날짜별 매칭 리스트 조회
    @GetMapping("/matches-by-date")
    public ResponseEntity<List<MatchApplication>> getMatchesByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<MatchApplication> matchesByDate = matchService.getMatchesByDate(date);

        return ResponseEntity.status(HttpStatus.OK).body(matchesByDate);
    }



}
