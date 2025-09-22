package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;

import java.util.List;

public interface FriendRequestService {
    List<FriendResponseDto> getReceivedFriendRequests(int receiverUserId);

    List<FriendResponseDto> getSentFriendRequests(int senderUserId);

    void deleteSentFriendRequest(int senderUserId, int receiverUserId);

    void deleteReceivedFriendRequest(int receiverUserId, int senderUserId);

    void sendFriendRequest(int senderUserId, int receiverUserId);

    void acceptFriendRequest(int senderUserId, int receiverUserId);
}