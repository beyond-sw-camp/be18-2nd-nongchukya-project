package com.beyond.sportsmatch.auth.model.service;

import com.beyond.sportsmatch.auth.model.dto.request.LoginRequestDto;
import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.repository.RefreshTokenRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.auth.model.security.jwt.JwtTokenProvider;
import com.beyond.sportsmatch.auth.model.entity.RefreshToken;
import com.beyond.sportsmatch.domain.user.model.entity.User;
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


    // 로그인 → AccessToken + RefreshToken 발급
    @Transactional
    public TokenResponseDto login(LoginRequestDto dto, HttpServletResponse response) {
        // 1. 유저 조회
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // 3. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getLoginId(),
                user.getRole().name(),
                user.getNickname()
        );
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getLoginId());

        // 4. RefreshToken DB 저장
        createOrUpdateRefreshToken(user, refreshTokenValue);

        // 5. 쿠키 저장
        response.addCookie(createRefreshTokenCookie(refreshTokenValue));

        return new TokenResponseDto(accessToken, refreshTokenValue);
    }


    // RefreshToken 검증 후 AccessToken 재발급
    @Transactional
    public TokenResponseDto refresh(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 RefreshToken 추출
        String refreshTokenValue = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token이 존재하지 않습니다."));

        // 2. RefreshToken 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        // 3. 토큰에서 loginId 추출
        String loginId = jwtTokenProvider.getUserId(refreshTokenValue);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 4. DB에 저장된 RefreshToken 비교
        RefreshToken savedToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("DB에 저장된 Refresh Token이 없습니다."));

        if (!savedToken.getToken().equals(refreshTokenValue)) {
            throw new IllegalArgumentException("DB의 Refresh Token과 일치하지 않습니다.");
        }
        if (savedToken.isExpired()) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다. 다시 로그인하세요.");
        }

        // 5. 새 AccessToken + RefreshToken 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(
                loginId,
                user.getRole().name(),
                user.getNickname()
        );
        String newRefreshTokenValue = jwtTokenProvider.createRefreshToken(loginId);

        savedToken.updateToken(newRefreshTokenValue, LocalDateTime.now().plusDays(90));
        refreshTokenRepository.save(savedToken);

        // 6. 쿠키 갱신
        response.addCookie(createRefreshTokenCookie(newRefreshTokenValue));

        return new TokenResponseDto(newAccessToken, newRefreshTokenValue);
    }


    private Cookie createRefreshTokenCookie(String refreshTokenValue) {
        Cookie cookie = new Cookie("refreshToken", refreshTokenValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 운영 환경에서는 true
        cookie.setPath("/api/v1/auth/");
        cookie.setMaxAge(60 * 60 * 24 * 90); // 3개월
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

    // 로그아웃
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 RefreshToken 추출
        String refreshTokenValue = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshTokenValue != null) {
            // 2. DB에서 RefreshToken 삭제
            refreshTokenRepository.findByToken(refreshTokenValue)
                    .ifPresent(refreshTokenRepository::delete);

            // 3. 쿠키 만료시켜서 클라이언트에서 삭제되게 함
            Cookie expiredCookie = new Cookie("refreshToken", null);
            expiredCookie.setHttpOnly(true);
            expiredCookie.setSecure(false); // 운영 환경에서는 true
            expiredCookie.setPath("/api/v1/auth/refresh");
            expiredCookie.setMaxAge(0); // 즉시 만료
            response.addCookie(expiredCookie);
        }
    }
}
