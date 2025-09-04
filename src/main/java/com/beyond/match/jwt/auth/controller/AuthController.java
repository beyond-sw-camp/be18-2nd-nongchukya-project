package com.beyond.match.jwt.auth.controller;

import com.beyond.match.jwt.auth.dto.LoginRequestDto;
import com.beyond.match.jwt.auth.dto.SignUpRequestDto;
import com.beyond.match.jwt.auth.dto.TokenResponseDto;
import com.beyond.match.user.model.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequestDto req) {
        authService.signUp(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // 프론트: Access 만료시 헤더로 userId/refresh 전달(또는 refresh JWT의 subject 사용)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestHeader("X-USER-ID") int userId,
                                                    @RequestHeader("X-REFRESH") String refresh) {
        return ResponseEntity.ok(authService.refresh(userId, refresh));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-USER-ID") int userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }
}
