package com.beyond.match.jwt.security;


import com.beyond.match.jwt.auth.model.security.UserDetailsImpl;
import com.beyond.match.user.model.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwt;
    //    private final UserMapper userMapper;
    private final UserRepository userRepository;
    // 인증,인가
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);
            return;
        }

        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            if (jwt.validate(token) && jwt.isAccessToken(token)) {
                Integer uid = jwt.userId(token);

                userRepository.findById(uid).ifPresent(u -> {
                    UserDetailsImpl userDetails = new UserDetailsImpl(u);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, // principal
                            null,          // credentials (보통 null 처리)
                            userDetails.getAuthorities()      // 권한 (ROLE_USER 등 넣을 수 있음)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }
        chain.doFilter(req, res);
    }
}