package com.beyond.match.chat.model.dto;

import lombok.Getter;

@Getter
public class ChatDto {
    private Long chatRoomId;
    private String sender;
    private String message;
}
