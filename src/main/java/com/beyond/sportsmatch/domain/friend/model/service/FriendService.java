package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;

import java.util.List;

public interface FriendService {
    List<FriendResponseDto> getFriends(int loginUserId);

    void deleteFriend(int loginUserId, int friendUserId);
}