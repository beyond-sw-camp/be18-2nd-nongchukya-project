package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendRequestDto;

import java.util.List;

public interface FriendService {
    List<FriendRequestDto> getFriends(int loginUserId);

    void deleteFriend(int loginUserId, int friendUserId);
}
