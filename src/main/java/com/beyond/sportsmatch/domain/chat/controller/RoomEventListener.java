package com.beyond.sportsmatch.domain.chat.controller;

import com.beyond.sportsmatch.domain.chat.model.dto.RoomDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RoomEventListener {
    private final SimpMessagingTemplate messageTemplate;

    @EventListener
    public void onRoomDeleted(RoomDeletedEvent event) {
        messageTemplate.convertAndSend(
                "/sub/chat/" + event.roomId(),
                Map.of("type", "ROOM_DELETED", "message", event.message())
        );
    }
}
