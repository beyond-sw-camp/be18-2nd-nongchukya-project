package com.beyond.sportsmatch.auth.model.service;

import com.beyond.sportsmatch.auth.model.dto.request.LoginRequestDto;
import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.entity.RefreshToken;
import com.beyond.sportsmatch.auth.model.repository.RefreshTokenRepository;
import com.beyond.sportsmatch.auth.model.security.jwt.JwtTokenProvider;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 로그인 → AccessToken + RefreshToken 발급
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto dto, HttpServletResponse response) {
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getLoginId(),
                user.getRole().name(),
                user.getNickname()
        );
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getLoginId());

        createOrUpdateRefreshToken(user, refreshTokenValue);
        response.addCookie(createRefreshTokenCookie(refreshTokenValue));

        return new TokenResponseDto(accessToken, refreshTokenValue, user.getNickname());
    }

    /**
     * RefreshToken 검증 후 AccessToken 재발급
     */
    @Transactional
    public TokenResponseDto refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshTokenValue = Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token이 존재하지 않습니다."));

        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        String loginId = jwtTokenProvider.getUserId(refreshTokenValue);
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        RefreshToken savedToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("DB에 저장된 Refresh Token이 없습니다."));

        if (!savedToken.getToken().equals(refreshTokenValue)) {
            throw new IllegalArgumentException("DB의 Refresh Token과 일치하지 않습니다.");
        }
        if (savedToken.isExpired()) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다. 다시 로그인하세요.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(
                loginId,
                user.getRole().name(),
                user.getNickname()
        );
        String newRefreshTokenValue = jwtTokenProvider.createRefreshToken(loginId);

        savedToken.updateToken(newRefreshTokenValue, LocalDateTime.now().plusDays(90));
        refreshTokenRepository.save(savedToken);

        response.addCookie(createRefreshTokenCookie(newRefreshTokenValue));

        return new TokenResponseDto(newAccessToken, newRefreshTokenValue, user.getNickname());
    }

    private Cookie createRefreshTokenCookie(String refreshTokenValue) {
        Cookie cookie = new Cookie("refreshToken", refreshTokenValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // ✅ HTTPS 환경 권장
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 90); // 90일
        return cookie;
    }

    private void createOrUpdateRefreshToken(User user, String tokenValue) {
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(tokenValue)
                        .expiryDate(LocalDateTime.now().plusDays(90))
                        .build()
        );
    }

    /**
     * 로그아웃 → RefreshToken DB 삭제 및 쿠키 만료 처리
     */
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;

        Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .forEach(tokenValue -> {
                    refreshTokenRepository.findByToken(tokenValue)
                            .ifPresent(refreshTokenRepository::delete);
                });

        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);
    }
}
