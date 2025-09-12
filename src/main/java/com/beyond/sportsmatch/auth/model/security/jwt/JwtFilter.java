package com.beyond.sportsmatch.auth.model.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * - 매 요청마다 Authorization 헤더에서 토큰 추출
 * - 유효하면 Authentication 생성 후 SecurityContext에 저장
 */
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    // JWT에서 Authentication 생성
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);

                    // SecurityContextHolder에 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT 인증 성공 - user: {}", authentication.getName());
                } else {
                    log.warn("유효하지 않은 JWT 토큰: {}", token);
                }
            } catch (Exception e) {
                log.error("JWT 인증 처리 중 예외 발생: {}", e.getMessage(), e);
            }
        } else {
            log.trace("요청에 Authorization 헤더 없음");
        }

        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
