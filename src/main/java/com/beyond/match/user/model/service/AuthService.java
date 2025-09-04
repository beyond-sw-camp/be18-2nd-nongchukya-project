package com.beyond.match.user.model.service;


import com.beyond.match.jwt.auth.model.dto.LoginRequestDto;
import com.beyond.match.jwt.auth.model.dto.SignUpRequestDto;
import com.beyond.match.jwt.auth.model.dto.TokenResponseDto;
import com.beyond.match.jwt.security.JwtTokenProvider;
import com.beyond.match.jwt.security.TokenHashUtil;
import com.beyond.match.jwt.token.repository.RefreshTokenRepository;
import com.beyond.match.jwt.token.vo.RefreshToken;
import com.beyond.match.user.model.repository.UserRepository;
import com.beyond.match.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    //    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwt;

    public void signUp(SignUpRequestDto req) {
        User u = new User();
        u.setId(req.id());
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setName(req.name());
        u.setNickname(req.nickname());
        u.setStatus("A"); // Active 기본값
//        u.setGender(req.gender());
        u.setGender("M");
//        u.setDmOption(req.dm_option());
        u.setDmOption(true);
        userRepository.save(u);
    }
    // 테스트

    public TokenResponseDto login(LoginRequestDto req) {
        User u = userRepository.findById(req.id())
                .orElseThrow(() -> new BadCredentialsException("아이디/비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.password(), u.getPassword()))
            throw new BadCredentialsException("아이디/비밀번호가 올바르지 않습니다.");

        String access  = jwt.createAccessToken(u.getUserId(), u.getNickname());
        String refresh = jwt.createRefreshToken(u.getUserId());

        // Refresh 평문 저장 금지 → 해시 보관
        String hash = TokenHashUtil.sha256Base64(refresh);
        RefreshToken rt = new RefreshToken(u.getUserId(), hash, jwt.refreshExpiryLdt());
        refreshTokenRepository.save(rt);

        return new TokenResponseDto(access, refresh, jwt.accessTtlSeconds());
    }

    public TokenResponseDto refresh(int userId, String refreshToken) {
        String savedHash = refreshTokenRepository.findHashByUserId(userId).orElse(null);
        if (savedHash == null
                || !jwt.validate(refreshToken) || !jwt.isRefreshToken(refreshToken)
                || !TokenHashUtil.matchesSha256Base64(refreshToken, savedHash)) {
            throw new BadCredentialsException("리프레시 토큰 무효");
        }

        String newAccess  = jwt.createAccessToken(userId, null);
        String newRefresh = jwt.createRefreshToken(userId);
        refreshTokenRepository.save(new RefreshToken(
                userId,
                TokenHashUtil.sha256Base64(newRefresh),
                jwt.refreshExpiryLdt()
        ));

        return new TokenResponseDto(newAccess, newRefresh, jwt.accessTtlSeconds());
    }

    public void logout(int userId) {
        refreshTokenRepository.deleteById(userId);
    }
}