package com.beyond.match.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyChatListResDto {
    private int roomId;
    private String roomName;
    private String isGroupChat;
    private int unReadCount;
}
