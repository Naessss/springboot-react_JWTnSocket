package com.example.demo.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 메시징 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 핸드셰이크 엔드포인트. React에서 ws(s)://<host>/ws 로 접속
        registry.addEndpoint("/ws")
                // 테스트에서는 모든 오리진 허용. 운영에서는 특정 도메인만 허용 권장
                .setAllowedOriginPatterns("*");
                // .withSockJS(); // 필요 시 SockJS 폴백(테스트 MVP에선 생략 가능)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버로 보내는 목적지 prefix (클라 SEND → /app/**)
        registry.setApplicationDestinationPrefixes("/app");
        // 브로커(서버가 브로드캐스트할 토픽 prefix)
        registry.enableSimpleBroker("/topic");
        // DM 큐를 쓰려면 "/queue"도 추가 가능
        // registry.enableSimpleBroker("/topic", "/queue");
    }
}
