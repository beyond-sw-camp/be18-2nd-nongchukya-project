package com.beyond.sportsmatch.auth.model.service;

import com.beyond.sportsmatch.auth.model.dto.response.KakaoUserResponseDto;
import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.repository.RefreshTokenRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.auth.model.security.jwt.JwtTokenProvider;
import com.beyond.sportsmatch.auth.model.entity.RefreshToken;
import com.beyond.sportsmatch.domain.user.model.entity.Role;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 카카오 로그인 → 회원가입 or 로그인 → JWT 발급
     */
    @Transactional
    public TokenResponseDto kakaoLogin(String code, HttpServletResponse response) {
        try {
            // 1. 인가 코드로 카카오 AccessToken 발급
            String tokenJson = getAccessToken(code);
            String kakaoAccessToken = objectMapper.readTree(tokenJson).get("access_token").asText();

            // 2. AccessToken으로 사용자 정보 요청
            KakaoUserResponseDto kakaoUser = getUserInfo(kakaoAccessToken);

            String email = kakaoUser.getKakaoAccount().getEmail();
            String nickname = kakaoUser.getKakaoAccount().getProfile().getNickname();
            String profileImage = kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl();

            // 3. DB에서 사용자 확인 (없으면 회원가입)
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                return userRepository.save(
                        User.builder()
                                .loginId(email)               // 지금은 email = loginId
                                .email(email)
                                .password("kakao_oauth")      // 더미 패스워드
                                .name(nickname)
                                .nickname(nickname)
                                .gender("UNKNOWN")
                                .age(0)
                                .address("NONE")
                                .phoneNumber("NONE")
                                .dmOption(false)
                                .status("Y")
                                .profileImage(profileImage)
                                .role(Role.USER)
                                .build()
                );
            });

            // 4. JWT 발급
            String accessJwt = jwtTokenProvider.createAccessToken(
                    user.getLoginId(),
                    user.getRole().name(),
                    user.getNickname()
            );
            String refreshJwt = jwtTokenProvider.createRefreshToken(user.getLoginId());

            // 5. RefreshToken DB 저장
            createOrUpdateRefreshToken(user, refreshJwt);

            // 6. RefreshToken 쿠키 저장
            response.addCookie(createRefreshTokenCookie(refreshJwt));

            // 7. 최종 반환 (닉네임 포함)
            return new TokenResponseDto(accessJwt, refreshJwt, user.getNickname());

        } catch (Exception e) {
            log.error("카카오 로그인 실패", e);
            throw new RuntimeException("카카오 로그인 실패: " + e.getMessage());
        }
    }

    // 카카오 인가 코드 → AccessToken 교환
    private String getAccessToken(String code) throws IOException {
        URL url = new URL("https://kauth.kakao.com/oauth/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String body = "grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&code=" + code;

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) response.append(line);
            return response.toString();
        }
    }

    // 카카오 AccessToken → 사용자 정보 가져오기
    private KakaoUserResponseDto getUserInfo(String accessToken) throws IOException {
        URL url = new URL("https://kapi.kakao.com/v2/user/me");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) response.append(line);
            return objectMapper.readValue(response.toString(), KakaoUserResponseDto.class);
        }
    }

    // RefreshToken 쿠키 생성
    private Cookie createRefreshTokenCookie(String refreshTokenValue) {
        Cookie cookie = new Cookie("refreshToken", refreshTokenValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 운영 환경에서는 true 권장
        cookie.setPath("/api/v1/auth/");
        cookie.setMaxAge(60 * 60 * 24 * 90); // 3개월
        return cookie;
    }

    // RefreshToken DB 저장/갱신
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
}
