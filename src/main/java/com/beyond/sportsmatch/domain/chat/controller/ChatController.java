package com.beyond.sportsmatch.domain.chat.controller;

import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.dto.BaseResponseDto;
import com.beyond.sportsmatch.domain.chat.model.dto.ChatDto;
import com.beyond.sportsmatch.domain.chat.model.dto.ChatRoomListResDto;
import com.beyond.sportsmatch.domain.chat.model.dto.MyChatListResDto;
import com.beyond.sportsmatch.domain.chat.model.dto.UserListResDto;
import com.beyond.sportsmatch.domain.chat.model.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/chatrooms")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/group/create")
    public ResponseEntity<BaseResponseDto<String>> createGroupRoom(@RequestParam String roomName) {
        chatService.createGroupRoom(roomName);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "채팅방 생성에 성공하였습니다."));
    }

    // 개인 채팅방 개설 또는 기존 roomId return
    @PostMapping("/private/create")
    public ResponseEntity<BaseResponseDto<Integer>> createOrGetPrivateRoom(@RequestParam String otherNickname) {
        int roomId = chatService.getOrCreatePrivateRoom(otherNickname);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, roomId));
    }

    // 매칭방리스트조회
    @GetMapping
    public ResponseEntity<BaseResponseDto<ChatRoomListResDto>> getAllRooms() {
        List<ChatRoomListResDto> chatRoomListResDtos = chatService.getGroupChatRooms();
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, chatRoomListResDtos));
    }

    // 그룹채팅방참여
    @PostMapping("/group/{roomId}")
    public ResponseEntity<BaseResponseDto<String>> joinGroupChatRoom(@PathVariable int roomId) {
        chatService.addParticipantToGroupChat(roomId);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "채팅 참여에 성공하였습니다."));
    }

    // 채팅방 들어갈때 db에 쌓여있는 메세지들 불러오기
    @GetMapping("/history/{roomId}")
    public ResponseEntity<BaseResponseDto<ChatDto>> getChatHistory(@PathVariable int roomId) {
        List<ChatDto> chatDtos = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, chatDtos));
    }

    // 채팅메시지 읽음처리
    @PostMapping("/{roomId}/read")
    public ResponseEntity<BaseResponseDto<String>> messageRead(@PathVariable int roomId) {
        chatService.messageRead(roomId);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "읽음 처리에 성공하였습니다."));
    }

    // 내 채팅방 목록 조회 : roomId, roomName, 그룹채팅여부, 메시지 읽음 개수
    @GetMapping("/my/rooms")
    public ResponseEntity<BaseResponseDto<MyChatListResDto>> getMyRooms() {
        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms();
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, myChatListResDtos));
    }

    // 채팅방 나가기
    @DeleteMapping("/group/{roomId}/leave")
    public ResponseEntity<BaseResponseDto<String>> leaveGroup(@PathVariable int roomId) {
        chatService.leaveGroupChatRoom(roomId);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "나가기에 성공하였습니다."));
    }
    // 1대1 채팅방 나가기
    @DeleteMapping("/private/{roomId}/leave")
    public ResponseEntity<BaseResponseDto<String>> leavePrivate(@PathVariable int roomId) {
        chatService.leavePrivateChatRoom(roomId);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "나가기에 성공하였습니다."));
    }

    @GetMapping("/{roomId}/exists")
    public ResponseEntity<BaseResponseDto<String>> existsRoom(@PathVariable int roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatService.assertAccessible(roomId, userDetails);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "채팅방이 존재합니다."));
    }

    @GetMapping("/{roomId}/users")
    public ResponseEntity<BaseResponseDto<UserListResDto>>  getUsers(@PathVariable int roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserListResDto> userListResDtos = chatService.getUsersNick(roomId, userDetails);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, userListResDtos));
    }




}
