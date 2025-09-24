package com.beyond.sportsmatch.domain.match.controller;


import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.dto.BaseResponseDto;
import com.beyond.sportsmatch.common.dto.ItemsResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.CompletedMatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResultRequestDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResponseDto;
import com.beyond.sportsmatch.domain.match.model.dto.MatchResultResponseDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.service.MatchService;
import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
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

import java.net.URI;
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
    public ResponseEntity<BaseResponseDto<MatchApplicationResponseDto>> createMatch(@RequestBody MatchApplicationRequestDto requestDto,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 로그인된 유저 정보 가져오기
        User user = userDetails.getUser();

        MatchApplication matchApplication = matchService.saveMatch(requestDto, user);

        return ResponseEntity.created(URI.create("/api/v1/match-service/match-applications/" + matchApplication.getMatchApplicationId())).
                body(new BaseResponseDto<>(HttpStatus.CREATED, new MatchApplicationResponseDto(matchApplication)));
    }

    // 매칭 조회
    @GetMapping("/match-applications/{applicationId}")
    public ResponseEntity<MatchApplicationResponseDto> getMatch(@PathVariable("applicationId") int applicationId) {

        MatchApplication matchApplication = matchService.getMatch(applicationId);

        return ResponseEntity.status(HttpStatus.OK).body(new MatchApplicationResponseDto(matchApplication));
    }

    // 매칭 신청 취소(매칭 신청 삭제)
    @DeleteMapping("/match-applications/{applicationId}")
    public ResponseEntity<BaseResponseDto<MatchApplication>> deleteMatch(@PathVariable("applicationId") int applicationId) {

        MatchApplication matchApplication = matchService.deleteMatch(applicationId);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, matchApplication));
    }

    // 매칭 신청 리스트 조회 (전체)
    @GetMapping("/match-applications")
    public ResponseEntity<List<MatchApplication>> getMatchByMatchApplicationId() {
        List<MatchApplication> matchList = matchService.getMatchApplications();

        return ResponseEntity.status(HttpStatus.OK).body(matchList);
    }

    // 매칭 신청 리스트 조회 (사용자 본인것만)
    @GetMapping("/me/match-applications")
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
    @GetMapping("/matches")
    public ResponseEntity<Set<String>> getMatchingList() {
        Set<String> matchingList = matchService.getMatches();

        return ResponseEntity.status(HttpStatus.OK).body(matchingList);
    }

    // 매칭 중인 리스트 조회 (사용자 본인것만)
    @GetMapping("/me/matches")
    public ResponseEntity<ItemsResponseDto<MatchResponseDto>> getMatchingList(@PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        int totalCount = matchService.getWaitingCountForUser(user);
        List<MatchResponseDto> matches = matchService.getMatchesByUser(user, pageable);

        return ResponseEntity.ok(
                new ItemsResponseDto<>(HttpStatus.OK, matches, pageable.getPageSize(), totalCount));
    }

    // 마감 임박 상위 5개 조회
    @GetMapping("/imminent-matches")
    public ResponseEntity<List<MatchResponseDto>> getImminentMatches() {
        List<MatchResponseDto> imminentMatches = matchService.getImminentMatches();

        return ResponseEntity.ok(imminentMatches);
    }

    // 날짜별 매칭 리스트 조회
    @GetMapping("/matches-by-date")
    public ResponseEntity<ItemsResponseDto<MatchResponseDto>> getMatchesByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                               @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        int totalCount = matchService.getTotalCount();
        List<MatchResponseDto> matches = matchService.getMatchesByDate(date);

        return ResponseEntity.ok(
                new ItemsResponseDto<>(HttpStatus.OK, matches, pageable.getPageSize(), totalCount));
    }

    // 매칭 완료 리스트 조회
//    @GetMapping("/completed-matches")
//    public ResponseEntity<List<MatchCompleted>> getCompletedMatches() {
//        List<MatchCompleted> completedMatches = matchService.getCompletedMatches();
//
//        return ResponseEntity.status(HttpStatus.OK).body(completedMatches);
//    }

    // 매칭 완료 리스트 조회
    @GetMapping("/completed-matches")
    public ResponseEntity<ItemsResponseDto<CompletedMatchResponseDto>> getCompletedMatches(@PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        int totalCount = matchService.getCompletedCountForUser(user);
        List<CompletedMatchResponseDto> completedMatches = matchService.getCompletedMatches(user, pageable);

        return ResponseEntity.ok(new ItemsResponseDto<>(HttpStatus.OK, completedMatches, pageable.getPageSize(), totalCount));
    }

    // 경기 결과 등록
    @PostMapping("/completed-matches/{matchId}/match-results")
    public ResponseEntity<MatchResultResponseDto> saveResult(@PathVariable("matchId") int matchId,
                                                  @RequestBody MatchResultRequestDto dto) {
        MatchResultResponseDto matchResultResponseDto = matchService.saveMatchResult(matchId, dto);

        return ResponseEntity.status(HttpStatus.OK).body(matchResultResponseDto);
    }

    // 경기 결과 리스트 조회
    @GetMapping("/match-results")
    public ResponseEntity<List<MatchResultResponseDto>> getResult(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<MatchResultResponseDto> results = matchService.getMatchResults(user);

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    // 종목 조회
    @GetMapping("/sport")
    public ResponseEntity<List<Sport>> getSports() {
        List<Sport> sports = matchService.getSports();

        return ResponseEntity.status(HttpStatus.OK).body(sports);
    }

}
