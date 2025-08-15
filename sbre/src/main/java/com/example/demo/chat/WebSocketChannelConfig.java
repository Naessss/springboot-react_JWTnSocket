package com.example.demo.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketChannelConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtStompChannelInterceptor jwtInterceptor;

    public WebSocketChannelConfig(JwtStompChannelInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 클라이언트 → 서버 방향(STOMP INBOUND)에 인터셉터 등록
        registration.interceptors(jwtInterceptor);
    }
}