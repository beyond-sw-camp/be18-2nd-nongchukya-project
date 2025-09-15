package com.beyond.sportsmatch.domain.notification.controller;

import com.beyond.sportsmatch.auth.model.security.jwt.JwtTokenProvider;
import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.domain.notification.model.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
public class SseController {
    private final NotificationSseService sseService;
    private final JwtTokenProvider jwtTokenProvider; // JWT 쓸 때만

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam(required = false) String token) {
        // 1) 시큐리티 컨텍스트에서 먼저 시도 (세션/쿠키 인증일 때)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginId = null;
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            // UserDetailsImpl 여부와 상관없이 username(=loginId) 얻기
            loginId = auth.getName();
        }

        // 2) 없으면 쿼리 파라미터의 JWT로 인증 (EventSource는 Authorization 헤더를 못 보냄)
        if (loginId == null && token != null && !token.isBlank()) {
            Authentication jwtAuth = jwtTokenProvider.getAuthentication(token);
            loginId = jwtAuth.getName(); // UserDetails#getUsername() == loginId
        }

        if (loginId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No auth for SSE");
        }

        // (선택) 캐시 방지 헤더는 Spring이 기본 세팅하지만 필요시 필터에서 추가
        return sseService.subscribe(loginId);
    }
}
