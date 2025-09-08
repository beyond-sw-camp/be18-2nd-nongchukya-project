package com.beyond.match.chat.config;

import com.beyond.match.chat.model.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
    @Value("${jwt.secret}")
    private String secretKey;

    private final ChatService chatService;

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
        if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            System.out.println("subscribe 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);
            // 토큰 검증
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            String nickname = (String) claims.get("nick");
            String roomId = accessor.getDestination().split("/")[3];
            log.info("nickname:{} roomId:{}", nickname, roomId);
            if(!chatService.isRoomParticipant(nickname, Integer.parseInt(roomId))) {
                throw new AuthenticationServiceException("해당 room에 권한이 없습니다.");
            }
        }
        return message;
    }
}
