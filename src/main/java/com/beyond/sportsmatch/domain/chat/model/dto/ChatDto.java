package com.beyond.sportsmatch.domain.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
    private String senderNickname;
    private String message;
}
