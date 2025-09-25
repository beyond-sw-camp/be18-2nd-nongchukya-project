package com.beyond.sportsmatch.auth.controller;


import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.service.KakaoAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    // 카카오 로그인 콜백
    @GetMapping("/callback")
    public ResponseEntity<TokenResponseDto> kakaoCallback(
            @RequestParam String code,
            HttpServletResponse response
    ) {
        TokenResponseDto tokenResponse = kakaoAuthService.kakaoLogin(code, response);
        return ResponseEntity.ok(tokenResponse);
    }
}
