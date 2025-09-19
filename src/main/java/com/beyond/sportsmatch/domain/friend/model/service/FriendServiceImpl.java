package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;
import com.beyond.sportsmatch.domain.friend.model.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;

    @Override
    public List<FriendResponseDto> getFriends(int loginUserId) {

        return friendRepository.findFriendsByUserId(loginUserId);
    }

    @Override
    @Transactional
    public void deleteFriend(int loginUserId, int friendUserId) {
        friendRepository.deleteByLoginUserIdUserIdAndFriendUserIdUserId(loginUserId, friendUserId);
        friendRepository.deleteByLoginUserIdUserIdAndFriendUserIdUserId(friendUserId, loginUserId);

    }

}
