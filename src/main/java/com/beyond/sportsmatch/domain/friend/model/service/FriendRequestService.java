package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendRequestDto;

import java.util.List;

public interface FriendRequestService {
    List<FriendRequestDto> getReceivedFriendRequests(int receiverUserId);

    List<FriendRequestDto> getSentFriendRequests(int senderUserId);

    void deleteSentFriendRequest(int senderUserId, int receiverUserId);

    void deleteReceivedFriendRequest(int receiverUserId, int senderUserId);

    void sendFriendRequest(int senderUserId, String receiverUserNickname);

    void acceptFriendRequest(int senderUserId, int receiverUserId);
}
