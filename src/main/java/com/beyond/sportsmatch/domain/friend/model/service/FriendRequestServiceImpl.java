package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.common.exception.SportsMatchException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;
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
    public List<FriendResponseDto> getReceivedFriendRequests(int receiverUserId) {

        return friendRequestRepository.findReceivedFriendRequestsByUserId(receiverUserId);
    }

    @Override
    public List<FriendResponseDto> getSentFriendRequests(int senderUserId) {

        return friendRequestRepository.findSentFriendRequestsByUserId(senderUserId);
    }

    @Override
    public void deleteSentFriendRequest(int senderUserId, int receiverUserId) {
        boolean exists = friendRequestRepository.existsBySenderUserIdAndReceiverUserId(senderUserId, receiverUserId);

        if (!exists) {
            throw new SportsMatchException(ExceptionMessage.REQUEST_NOT_FOUND);
        }

        friendRequestRepository.deleteBySenderUserIdAndReceiverUserId(senderUserId, receiverUserId);
    }

    @Override
    public void deleteReceivedFriendRequest(int receiverUserId, int senderUserId) {
        boolean exists = friendRequestRepository.existsBySenderUserIdAndReceiverUserId(receiverUserId, senderUserId);

        if (!exists) {
            throw new SportsMatchException(ExceptionMessage.REQUEST_NOT_FOUND);
        }
        friendRequestRepository.deleteByReceiverUserIdAndSenderUserId(receiverUserId, senderUserId);
    }

    @Override
    @Transactional
    public void sendFriendRequest(int senderUserId, int receiverUserId) {
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new SportsMatchException(ExceptionMessage.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverUserId)
                .orElseThrow(() -> new SportsMatchException(ExceptionMessage.USER_NOT_FOUND));

        boolean exists = friendRequestRepository.existsBySenderUserIdAndReceiverUserIdAndStatus(sender, receiver, "Pending");
        if (exists) {
            throw new SportsMatchException(ExceptionMessage.ALREADY_SENT_REQUEST);
        }


        FriendRequest request = new FriendRequest();
        request.setSenderUserId(sender);
        request.setReceiverUserId(receiver);
        request.setStatus("Pending");
        friendRequestRepository.save(request);
    }

    @Override
    public void acceptFriendRequest(int senderUserId, int receiverUserId) {
        // 친구인지 먼저 확인
        boolean alreadyFriend = friendRepository.existsByLoginUserIdUserIdAndFriendUserIdUserId(senderUserId, receiverUserId);
        if (alreadyFriend) {
            throw new SportsMatchException(ExceptionMessage.ALREADY_FRIEND);
        }


        friendRequestRepository.deleteByReceiverUserIdAndSenderUserId(receiverUserId, senderUserId);



        // 2. Friend 엔티티에 양방향 저장
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new SportsMatchException(ExceptionMessage.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverUserId)
                .orElseThrow(() -> new SportsMatchException(ExceptionMessage.USER_NOT_FOUND));

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
