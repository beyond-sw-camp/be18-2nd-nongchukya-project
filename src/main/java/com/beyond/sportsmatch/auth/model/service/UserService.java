package com.beyond.sportsmatch.auth.model.service;


import com.beyond.sportsmatch.auth.model.dto.request.SignUpRequestDto;
import com.beyond.sportsmatch.auth.model.dto.response.FindIdResponseDto;
import com.beyond.sportsmatch.auth.model.dto.response.PasswordVerifyResponseDto;
import com.beyond.sportsmatch.auth.model.dto.response.TokenResponseDto;
import com.beyond.sportsmatch.auth.model.repository.RefreshTokenRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.auth.model.security.jwt.JwtTokenProvider;
import com.beyond.sportsmatch.auth.model.entity.RefreshToken;
import com.beyond.sportsmatch.domain.user.model.entity.Role;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(SignUpRequestDto dto, MultipartFile profileImage) {
        // 1. 이메일 중복 체크
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. 나이 계산
        Integer calculatedAge = null;
        if (dto.getBirthdate() != null) {
            calculatedAge = Period.between(dto.getBirthdate(), LocalDate.now()).getYears();
        }

        // 4. 프로필 이미지 저장 (없으면 null)
        String imagePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 실행 경로 기준 절대 경로
                String uploadDir = System.getProperty("user.dir") + "/uploads";
                File dir = new File(uploadDir);
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new RuntimeException("업로드 디렉토리 생성 실패: " + uploadDir);
                }

                // 파일명에 UUID 붙여 충돌 방지
                String filename = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                File dest = new File(dir, filename);

                profileImage.transferTo(dest); // 실제 저장

                // DB에 저장할 경로 (정적 리소스 매핑 필요)
                imagePath = "/uploads/" + filename;

                log.info("프로필 이미지 저장 성공: {}", dest.getAbsolutePath());

            } catch (IOException e) {
                log.error("프로필 이미지 저장 실패", e);
                throw new RuntimeException("프로필 이미지 저장 실패", e);
            }
        }


        // 5. User 엔티티 생성
        User user = User.builder()
                .loginId(dto.getLoginId())
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .name(dto.getName())
                .birthDate(dto.getBirthdate()) // dto가 LocalDate 타입이면 그대로, String이면 LocalDate.parse 필요
                .age(calculatedAge)
                .gender(dto.getGender())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .dmOption(dto.getDmOption())
                .status("ACTIVE")
                .profileImage(imagePath) // ✅ 카카오 로그인과 동일하게 이 필드만 사용
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }


    /**
     * 아이디 찾기
     */
    @Transactional(readOnly = true)
    public FindIdResponseDto findIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다."));

        String masked = maskUserId(user.getLoginId());
        return new FindIdResponseDto(masked);
    }

    private String maskUserId(String loginId) {
        if (loginId.length() <= 3) {
            return loginId.charAt(0) + "*".repeat(loginId.length() - 1);
        }

        int start = 2;
        int end = loginId.length() - 2;
        StringBuilder sb = new StringBuilder();
        sb.append(loginId, 0, start);
        for (int i = start; i < end; i++) sb.append('*');
        sb.append(loginId.substring(end));
        return sb.toString();
    }

    /**
     * 비밀번호 확인
     */
    @Transactional(readOnly = true)
    public PasswordVerifyResponseDto verifyPassword(String loginId, String rawPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return new PasswordVerifyResponseDto(user.getUserId());
    }

    /**
     * loginId로 사용자 찾기
     */
    @Transactional(readOnly = true)
    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    /**
     * 비밀번호 변경 + 토큰 재발급
     */
    @Transactional
    public TokenResponseDto changePassword(User user, String currentPassword, String newPassword, HttpServletResponse response) {
        // 현재 비밀번호 검증

        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }
        // 1. 새 비밀번호 암호화 후 업데이트
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 2. 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole().name(), user.getNickname());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId());

        // 3. RefreshToken 갱신 (DB + 쿠키)
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(newRefreshToken)
                        .expiryDate(LocalDateTime.now().plusDays(90))
                        .build()
        );

        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 90);
        response.addCookie(cookie);

        // 4. 응답 반환
        return new TokenResponseDto(newAccessToken, newRefreshToken, user.getNickname());
    }

    // 회원 탈퇴
    @Transactional
    public void deactivateUser(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // 엔티티 메서드 없이 바로 상태 변경
        user.setStatus("DEACTIVATED");
        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    @Transactional
    public void resetPasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다."));

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
