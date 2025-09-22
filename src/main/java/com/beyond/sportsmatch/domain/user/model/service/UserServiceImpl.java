package com.beyond.sportsmatch.domain.user.model.service;

import com.beyond.sportsmatch.domain.friend.model.repository.FriendRepository;
import com.beyond.sportsmatch.domain.friend.model.repository.FriendRequestRepository;
import com.beyond.sportsmatch.domain.user.model.dto.UserResponseDto;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    public List<UserResponseDto> getSearchUsersByNickname(String nickname, int loginUserId) {
        List<User> users = userRepository.findByNicknameContaining(nickname);

        List<Integer> friendIds = friendRepository.findFriendIdsByUserId(loginUserId);

        int senderUserId = loginUserId;
        List<Integer> sentRequestIds = friendRequestRepository.findReceiverIdsBySenderId(senderUserId);

        return users.stream()
                .filter(user -> user.getUserId() != loginUserId)
                .filter(user -> !friendIds.contains(user.getUserId()))
                .filter(user -> !sentRequestIds.contains(user.getUserId()))
                .map(user -> new UserResponseDto(user.getUserId(), user.getNickname(), user.getProfileImage()))
                .toList();
    }
}
