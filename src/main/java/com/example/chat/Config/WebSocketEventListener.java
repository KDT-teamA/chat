package com.example.chat.Config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.chat.Chat.ChatMessage;
import com.example.chat.Chat.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//메소드를 자동으로 빈에 등록
@Component

@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    // SimpMessageSendingOperations는 메시지를 전송하는 데 사용되는 인터페이스입니다.
    // 이 인터페이스를 사용하여 특정 주제에 메시지를 보낼 수 있습니다
    private final SimpMessageSendingOperations messagingTemplate;
    
    // @EventListener는 Spring에서 이벤트를 수신하는 메소드를 정의하는 데 사용됩니다.
    // 이 어노테이션을 사용하여 특정 이벤트가 발생했을 때 호출되는 메소드를 정의할 수 있습니다.
    // 예를 들어, WebSocket 연결이 열리거나 닫힐 때 이벤트을 수신할 수 있습니다.
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        
        // 이벤트가 헤더 정보를 접근해서 정보를 읽을 수 있도록 StompHeaderAccessor를 사용합니다.
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // 헤더에서 사용자 이름을 가져옵니다.
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        // 사용자 이름이 null이 아니면 메시지를 전송합니다.
        if (username != null) {
            log.info("User Disconnected : " + username);

            // 채팅방에 로그아웃 정보를 전송합니다.
            var chatMessage = ChatMessage.builder()
                .type(MessageType.LEAVE) // 메시지 타입을 LEAVE로 설정
                .sender(username) // 메시지를 보낸 사람
                .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
