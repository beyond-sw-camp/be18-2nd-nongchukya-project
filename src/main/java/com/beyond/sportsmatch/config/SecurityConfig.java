package com.beyond.sportsmatch.config;


import com.beyond.sportsmatch.auth.model.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정
 * - 세션 대신 JWT 인증 사용
 * - 인증/인가 처리
 * - JwtFilter를 UsernamePasswordAuthenticationFilter 앞에 등록
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // AuthenticationManager (로그인 시 필요)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    // Spring Security 필터 체인
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(getCorsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "api/v1/auth/**",
                                "auth/kakao/callback",
                                "/ws/**",
                                "/api/v1/sse/**"
                        ).permitAll()
                        // 임박 매칭 리스트, 날짜별 매칭 리스트는 공개
                        .requestMatchers("api/v1/match-service/imminent-matches", "api/v1/match-service/matches-by-date").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource(){
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
//        configuration.setAllowedMethods(List.of("*")); //모든 HTTP메서드 허용
//        configuration.setAllowedHeaders(List.of("*")); //모든 헤더값 허용
//        configuration.setAllowCredentials(true); //자격증명허용
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration); //모든 url에 패턴에 대해 cors 허용 설정
//        return source;
//    }

    private static CorsConfigurationSource getCorsConfigurationSource() {
        return (request) -> {
            // CorsConfiguration 객체를 생성해서 CORS 설정을 한다.
            CorsConfiguration corsConfiguration = new CorsConfiguration();

            // CORS 요청에서 허용할 출처를 지정한다.
//            corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
            // 보안적으로 지양하는게 좋음(다 허용해주기 때문에)
            corsConfiguration.setAllowedOriginPatterns(List.of("*"));

            // CORS 요청에서 허용할 HTTP 메소드를 지정한다.
            corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

            // 클라이언트가 요청 시 사용할 수 있는 헤더를 지정한다.
            corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

            // 클라이언트가 응답에서 접근할 수 있는 헤더를 지정한다.
            corsConfiguration.setExposedHeaders(List.of("Authorization"));

            // 자격 증명(쿠키, 세션) 허용 여부를 설정한다.
            corsConfiguration.setAllowCredentials(true);

            // CORS Preflight 요청을 브라우저가 캐싱하는 시간(초 단위)을 설정한다.
            corsConfiguration.setMaxAge(3600L);

            return corsConfiguration;
        };
    }

}
