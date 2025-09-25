package com.beyond.sportsmatch.domain.mypage.controller;

import com.beyond.sportsmatch.domain.mypage.model.dto.UserLevelRequestDto;
import com.beyond.sportsmatch.domain.mypage.model.dto.UserLevelResponseDto;
import com.beyond.sportsmatch.domain.mypage.model.service.UserLevelService;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mypage/levels")
@RequiredArgsConstructor
public class UserLevelController {

    private final UserLevelService userLevelService;
    private final UserRepository userRepository;

    // 로그인한 사용자의 운동 레벨 목록 조회
    @GetMapping
    public ResponseEntity<List<UserLevelResponseDto>> getUserLevels(
            @AuthenticationPrincipal(expression = "user") User loginUser) {

        List<UserLevelResponseDto> dtos = userLevelService.getUserLevelDtosByLoginId(loginUser.getLoginId());
        return ResponseEntity.ok(dtos);
    }

    // 운동 레벨 수정/등록
    @PostMapping
    public ResponseEntity<UserLevelResponseDto> updateUserLevel(
            @AuthenticationPrincipal(expression = "user") User loginUser,
            @RequestBody UserLevelRequestDto requestDto) {

        UserLevelResponseDto updatedDto = userLevelService.updateUserLevel(
                loginUser,
                requestDto.getSportName(),
                requestDto.getLevelId(),
                requestDto.getInterest()
        );

        return ResponseEntity.ok(updatedDto);
    }
}