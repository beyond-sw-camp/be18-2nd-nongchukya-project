package com.beyond.sportsmatch.domain.friend.controller;


import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.exception.SportsMatchException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;
import com.beyond.sportsmatch.domain.friend.model.service.FriendRequestService;
import com.beyond.sportsmatch.domain.friend.model.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final FriendRequestService friendRequestService;

    @GetMapping("/list") // 친구 리스트 확인 메소드
    public ResponseEntity<List<FriendResponseDto>> getFriends(@AuthenticationPrincipal UserDetailsImpl loginUser) {
        if(loginUser == null){
            throw new SportsMatchException(ExceptionMessage.FRIEND_LIST_NOT_FOUND);
        }

        int loginUserId = loginUser.getUser().getUserId();
        List<FriendResponseDto> friends = friendService.getFriends(loginUserId);

        if (friends.isEmpty()) {

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/list/{friend-id}") // 친구 삭제 메소드
    public ResponseEntity<String> deleteFriend(@AuthenticationPrincipal UserDetailsImpl loginUser, @RequestParam int friendUserId) {
        int loginUserId = loginUser.getUser().getUserId();
        friendService.deleteFriend(loginUserId, friendUserId);
        return ResponseEntity.ok("친구 삭제 성공");
    }

    @GetMapping("/requests/received") // 받은 친구 추가 요청 리스트 확인 메소드
    public ResponseEntity<List<FriendResponseDto>> getReceivedFriendRequests(@AuthenticationPrincipal UserDetailsImpl receiverUser) {
        int receiverUserId = receiverUser.getUser().getUserId();
        List<FriendResponseDto> friendsRequest = friendRequestService.getReceivedFriendRequests(receiverUserId);

        return ResponseEntity.ok(friendsRequest);
    }

    @GetMapping("/requests/sent") // 보낸 친구 추가 요청 리스트 확인 메소드
    public ResponseEntity<List<FriendResponseDto>> getSentFriendRequests(@AuthenticationPrincipal UserDetailsImpl senderUser) {
        int senderUserId = senderUser.getUser().getUserId();

        List<FriendResponseDto> friendsRequest = friendRequestService.getSentFriendRequests(senderUserId);

        return ResponseEntity.ok(friendsRequest);
    }

    @PostMapping("/requests") // 친구 추가 요청 보내기 메소드
    public ResponseEntity<String> sendFriendRequest(@AuthenticationPrincipal UserDetailsImpl senderUser, @RequestParam String receiverUserNickname) {
        int senderUserId = senderUser.getUser().getUserId();
        friendRequestService.sendFriendRequest(senderUserId, receiverUserNickname);
        return ResponseEntity.ok("친구 요청 완료");
    }

    @PatchMapping("/requests/received/{request-id}") // 친구 추가 요청 수락 메소드
    public ResponseEntity<String> acceptFriendRequest(@AuthenticationPrincipal UserDetailsImpl receiverUser, @RequestParam int senderUserId) {
        int receiverUserId = receiverUser.getUser().getUserId();
        friendRequestService.acceptFriendRequest(senderUserId, receiverUserId);

        return ResponseEntity.ok("친구 요청 수락");
    }

    @DeleteMapping("/requests/sent/{request-id}") // 친구 추가 요청 철회 메소드
    public ResponseEntity<String> deleteSentFriendRequest(@AuthenticationPrincipal UserDetailsImpl loginUser, @RequestParam int receiverUserId) {
        int senderUserId = loginUser.getUser().getUserId();
        friendRequestService.deleteSentFriendRequest(senderUserId, receiverUserId);

        return ResponseEntity.ok("친구 요청 철회 완료");
    }

    @DeleteMapping("/requests/received/{request-id}") // 친구 추가 요청 거절 메소드
    public ResponseEntity<String> deleteReceivedFriendRequest(@RequestParam int senderUserId, @AuthenticationPrincipal UserDetailsImpl receiverUser) {
        int receiverUserId = receiverUser.getUser().getUserId();
        friendRequestService.deleteReceivedFriendRequest(receiverUserId, senderUserId);

        return ResponseEntity.ok("친구 요청 거절 완료");
    }
}
