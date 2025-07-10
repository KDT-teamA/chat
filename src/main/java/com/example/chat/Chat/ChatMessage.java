package com.example.chat.Chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private MessageType type; // 메시지 타입 (CHAT, JOIN, LEAVE)
    private String content; // 메시지 내용
    private String sender; // 메시지 보낸 사람
}
