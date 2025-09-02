package com.springboot.jwttest.jwt.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-exp-min}")
    private long accessExpMin;

    @Value("${jwt.refresh-exp-day}")
    private long refreshExpDay;

    private javax.crypto.SecretKey key;

    @PostConstruct
    void init() {
        Key k = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // 서명에 쓸 SecretKey 생성
        this.key = (SecretKey) k;
    }

    public String createAccessToken(int userId, String nickname) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId)) // sub: 사용자 식별자(PK)
                .claim("nick", nickname) // 편의를 위해 닉네임 넣음, DB에서는 닉네임 not null로 해야됨
                .claim("type", "access")
                .issuedAt(Date.from(now))
                // .issuer("your-service")
                // .id(Long.toHexString(System.nanoTime()))
                .expiration(Date.from(now.plus(accessExpMin, ChronoUnit.MINUTES)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String createRefreshToken(int userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshExpDay, ChronoUnit.DAYS)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isAccessToken(String token) {
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return "access".equals(claims.get("type"));
    }

    public boolean isRefreshToken(String token) {
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return "refresh".equals(claims.get("type"));
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) { return false; }
    }

    public Integer userId(String token) {
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return Integer.valueOf(claims.getSubject());
    }

    public long accessTtlSeconds(){ return accessExpMin * 60; }

    public LocalDateTime refreshExpiryLdt(){
        return LocalDateTime.now().plusDays(refreshExpDay);
    }

    public Date refreshExpiryDate(){
        return Date.from(refreshExpiryLdt().atZone(ZoneId.systemDefault()).toInstant());
    }
}
