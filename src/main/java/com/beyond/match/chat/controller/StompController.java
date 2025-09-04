package com.beyond.match.chat.controller;

import com.beyond.match.chat.model.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;

//    @MessageMapping("/chat/{chatRoomId}") // 클라이언트에서 특정 형태로 메시지를 발행시 메세지매핑 수신
//    @SendTo("/sub/chat/{chatRoomId}") // 해당 룸번호에 메시지를 발행하여 구독중인 클라이언트에게 메시지 전송
//    public String message(
//            // @MessageMapping 어노테이션으로 정의된 웹소켓 컨트롤러 내에서만 사용
//            @DestinationVariable Long chatRoomId,
//            @Payload String message
//            ){
//        log.info("chatRoomId:{}, message: {}",chatRoomId, message);
//
//        return message;
//    }

    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, ChatDto message){

        System.out.println(message.getMessage());
        messageTemplate.convertAndSend("/sub/chat/"+chatRoomId, message);
    }
}


