package com.example.chat.Chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    
    // 메시지 보내기
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public") // 메시지를 전달할 방 이름
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        // 메시지를 처리하고 반환
        return message; // 받은 메시지를 그대로 반환
    }

    // 대화방 참석
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public") // 메시지를 전달할 방 이름
    public ChatMessage addUser(@Payload ChatMessage message,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }
}
