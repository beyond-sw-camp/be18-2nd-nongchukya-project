package com.beyond.sportsmatch.domain.friend.model.service;


import com.beyond.sportsmatch.common.exception.SportsMatchException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;
import com.beyond.sportsmatch.domain.friend.model.entity.Friend;
import com.beyond.sportsmatch.domain.friend.model.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        Optional<Friend> friendOpt1 = friendRepository.findByLoginUserIdUserIdAndFriendUserIdUserId(loginUserId, friendUserId);
        Optional<Friend> friendOpt2 = friendRepository.findByLoginUserIdUserIdAndFriendUserIdUserId(friendUserId, loginUserId);

        if (friendOpt1.isEmpty() && friendOpt2.isEmpty()) {
            throw new SportsMatchException(ExceptionMessage.ALREADY_DELETE_FRIEND);
        }

        friendRepository.deleteByLoginUserIdUserIdAndFriendUserIdUserId(loginUserId, friendUserId);
        friendRepository.deleteByLoginUserIdUserIdAndFriendUserIdUserId(friendUserId, loginUserId);

    }

}
