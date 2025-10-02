package com.beyond.sportsmatch.auth.model.security.jwt;

import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

// 엄밀히 따지면 프로바이더는 헬퍼 메소드라 서비스단이 아님 새폴더 만드셔서 (utill이라던가 helper 라던가 거기에 이런 메소드들 몰아넣는게 편하죠?)
@Component
public class JwtTokenProvider {

//    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final SecretKey secretKey;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    // 토큰 만료 시간
    private final long accessTokenValidity = 1000L * 60 * 60 * 12;           // 15분
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 90; // 90일

    public JwtTokenProvider(SecretKey secretKey, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    /**
     * AccessToken 생성
     */
    public String createAccessToken(String loginId, String role, String nickname) {
        return Jwts.builder()
                .setSubject(loginId)
                .claim("role", role)  // 권한 포함
                .claim("nick", nickname)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(String loginId) {
        return buildToken(loginId, refreshTokenValidity);
    }

    /**
     * 공통 토큰 생성 로직
     */
    private String buildToken(String loginId, long validityMillis) {
        return Jwts.builder()
                .setSubject(loginId)
                .setExpiration(new Date(System.currentTimeMillis() + validityMillis))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token); // 서명 + 만료일 검증
            return true;
        } catch (ExpiredJwtException e) {
            // 만료
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // 위조, 잘못된 형식
            return false;
        }
    }

    /**
     * 토큰에서 userId 추출
     */
    public String getUserId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰에서 Authentication 객체 생성
     * → SecurityContext에 저장될 Authentication
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String loginId = claims.getSubject();
        String role = claims.get("role", String.class);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        UserDetails userDetails = new UserDetailsImpl(user);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }
}