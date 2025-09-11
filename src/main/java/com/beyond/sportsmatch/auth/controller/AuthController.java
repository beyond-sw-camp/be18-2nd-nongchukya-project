package com.beyond.sportsmatch.auth.controller;


import com.beyond.sportsmatch.auth.model.dto.request.LoginRequestDto;
import com.beyond.sportsmatch.auth.model.dto.request.SignUpRequestDto;
import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.service.AuthService;
import com.beyond.sportsmatch.auth.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

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

}
