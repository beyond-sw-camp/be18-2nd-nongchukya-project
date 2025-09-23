package com.beyond.sportsmatch.domain.mypage.controller;

import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.domain.mypage.model.dto.ProfileResponseDto;
import com.beyond.sportsmatch.domain.mypage.model.dto.UpdateProfileRequestDto;
import com.beyond.sportsmatch.domain.mypage.model.service.UserProfileService;
// 로그인 정보
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String loginId = userDetails.getUser().getLoginId();
        ProfileResponseDto profile = userProfileService.getProfile(loginId);

        return ResponseEntity.ok(profile);
    }

    // 프로필 업데이트
    @PutMapping("/profile")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdateProfileRequestDto dto) {

        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String loginId = userDetails.getUser().getLoginId();
        ProfileResponseDto updatedProfile = userProfileService.updateProfile(loginId, dto);

        return ResponseEntity.ok(updatedProfile);
    }
}
