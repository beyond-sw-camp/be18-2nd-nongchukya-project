package com.beyond.sportsmatch.auth.model.service;


import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security에서 Authentication 객체를 만들 때 사용하는 UserDetailsService 구현체
 *
 * - JwtTokenProvider.getAuthentication() 에서 호출됨
 * - 토큰에서 추출한 userId(loginId)를 기준으로 DB 조회
 * - User 엔티티를 Spring Security UserDetails 객체로 변환
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * username = JWT의 subject (loginId)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 유저 조회
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // User 엔티티 → Spring Security UserDetails 로 변환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLoginId())        // User 엔티티의 loginId
                .password(user.getPassword())       // 암호화된 비밀번호
                .roles("USER")                      // 권한 (DB 컬럼 추가하면 동적으로 매핑 가능)
                .build();
    }
}
