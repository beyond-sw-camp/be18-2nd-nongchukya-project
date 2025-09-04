package com.beyond.match.chat.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class StompHandler implements ChannelInterceptor {
    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    void init() {
        // 서명에 쓸 SecretKey 생성
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);


        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println("connect 요청시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);
            // 토큰 검증
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            System.out.println("토큰 검증 완료");
        }
        return message;
    }
}