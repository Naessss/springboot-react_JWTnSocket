package com.example.demo.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.demo.service.JwtService;

/**
 * STOMP CONNECT 단계에서 Authorization: Bearer <JWT>를 검증하고
 * Authentication을 세션에 주입하여 @MessageMapping 메서드에서 auth 사용 가능하게 함.
 */
@Component
public class JwtStompChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    public JwtStompChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        // CONNECT 프레임에서만 토큰 검증 및 Authentication 주입
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // STOMP Native 헤더에서 Authorization 추출(대소문자 혼동 방지로 둘 다 시도)
            String bearer = firstHeader(accessor, "Authorization");
            if (bearer == null) bearer = firstHeader(accessor, "authorization");

            if (!StringUtils.hasText(bearer) || !bearer.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing or invalid Authorization header for STOMP CONNECT");
            }
            String token = bearer.substring("Bearer ".length()).trim();

            if (!jwtService.validate(token)) {
                throw new IllegalArgumentException("Invalid or expired JWT for STOMP CONNECT");
            }

            // JwtService로부터 사용자명과 권한 복원
            String username = jwtService.getUsername(token);
            var authorities = jwtService.getAuthorities(token);

            // Principal 생성(자격 증명은 null, 권한은 JWT에서 복원)
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

            // 세션에 Authentication 주입 → 이후 @MessageMapping(Authentication)로 접근 가능
            accessor.setUser(auth);
        }

        return message;
    }

    private String firstHeader(StompHeaderAccessor accessor, String name) {
        List<String> values = accessor.getNativeHeader(name);
        return Optional.ofNullable(values).filter(v -> !v.isEmpty()).map(v -> v.get(0)).orElse(null);
    }
}