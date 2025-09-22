package com.beyond.sportsmatch.auth.controller;


import com.beyond.sportsmatch.auth.model.dto.request.LoginRequestDto;
import com.beyond.sportsmatch.auth.model.dto.request.SignUpRequestDto;
import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.service.AuthService;
import com.beyond.sportsmatch.auth.model.service.EmailService;
import com.beyond.sportsmatch.auth.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final EmailService emailService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequestDto dto) {
        System.out.println("회원가입 요청 DTO = " + dto);
        userService.signUp(dto);
        return ResponseEntity.ok("회원가입 성공!");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletResponse response) {

        TokenResponseDto tokenResponse = authService.login(dto, response);
        return ResponseEntity.ok(tokenResponse);
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        TokenResponseDto newAccessToken = authService.refresh(request, response);
        return ResponseEntity.ok(newAccessToken);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build(); // 204 반환
    }

    // 이메일 인증코드 발송
    @PostMapping("/send-email-code")
    public ResponseEntity<Map<String, Boolean>> sendEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 이메일 인증코드 검증
    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, Boolean>> verifyEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean success = emailService.verifyCode(email, code);
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestParam String loginId) {
        boolean available = userService.isLoginIdAvailable(loginId);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        userService.resetPasswordByEmail(email, newPassword);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "비밀번호가 성공적으로 재설정되었습니다. 로그인 후 이용해주세요."
        ));
    }

}
