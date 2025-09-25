package com.beyond.sportsmatch.domain.user.model.service;

import com.beyond.sportsmatch.domain.user.model.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getSearchUsersByNickname(String nickname, int loginUserId);
}
