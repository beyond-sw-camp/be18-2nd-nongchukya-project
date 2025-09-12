package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendRequestDto;
import com.beyond.sportsmatch.domain.friend.model.entity.Friend;
import com.beyond.sportsmatch.domain.friend.model.entity.FriendRequest;
import com.beyond.sportsmatch.domain.friend.model.repository.FriendRepository;
import com.beyond.sportsmatch.domain.friend.model.repository.FriendRequestRepository;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Override
    public List<FriendRequestDto> getReceivedFriendRequests(int receiverUserId) {

        return friendRequestRepository.findReceivedFriendRequestsByUserId(receiverUserId);
    }

    @Override
    public List<FriendRequestDto> getSentFriendRequests(int senderUserId) {

        return friendRequestRepository.findSentFriendRequestsByUserId(senderUserId);
    }

    @Override
    public void deleteSentFriendRequest(int senderUserId, int receiverUserId) {
        friendRequestRepository.deleteBySenderUserIdAndReceiverUserId(senderUserId, receiverUserId);
    }

    @Override
    public void deleteReceivedFriendRequest(int receiverUserId, int senderUserId) {
        friendRequestRepository.deleteByReceiverUserIdAndSenderUserId(receiverUserId, senderUserId);
    }

    @Override
    @Transactional
    public void sendFriendRequest(int senderUserId, String receiverUserNickname) {
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new RuntimeException("보낸 사람 없음"));
        User receiver = userRepository.findByNickname(receiverUserNickname)
                .orElseThrow(() -> new RuntimeException("받는 사람 없음"));

        boolean exists = friendRequestRepository.existsBySenderUserIdAndReceiverUserIdAndStatus(sender, receiver, "Pending");
        if (exists) {
            throw new RuntimeException("이미 요청한 친구입니다.");
        }


        FriendRequest request = new FriendRequest();
        request.setSenderUserId(sender);
        request.setReceiverUserId(receiver);
        request.setStatus("Pending");
        friendRequestRepository.save(request);
    }

    @Override
    public void acceptFriendRequest(int senderUserId, int receiverUserId) {
        friendRequestRepository.deleteByReceiverUserIdAndSenderUserId(receiverUserId, senderUserId);

        // 2. Friend 엔티티에 양방향 저장
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("보낸 유저를 찾을 수 없습니다."));
        User receiver = userRepository.findById(receiverUserId)
                .orElseThrow(() -> new IllegalArgumentException("받는 유저를 찾을 수 없습니다."));

        Friend friend1 = new Friend();
        friend1.setLoginUserId(sender);
        friend1.setFriendUserId(receiver);

        Friend friend2 = new Friend();
        friend2.setLoginUserId(receiver);
        friend2.setFriendUserId(sender);

        friendRepository.save(friend1);
        friendRepository.save(friend2);
    }

}
