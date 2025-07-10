package com.example.chat.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 연결할 엔드포인트를 등록
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 설정

        // 클라이언트가 서버로 메시지를 보낼 때 사용할 접두사를 설정
        // 예: /app /chat /send
        config.setApplicationDestinationPrefixes("/app");

        // 클라이언트가 구독할 수 있는 주제(prefix)를 설정
        // 예: /topic /chat
        config.enableSimpleBroker("/topic");
    }
}
