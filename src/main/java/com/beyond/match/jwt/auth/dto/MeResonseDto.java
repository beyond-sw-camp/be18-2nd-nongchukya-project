package com.beyond.match.jwt.auth.dto;

import com.beyond.match.user.model.vo.User;

public record MeResonseDto(
        Integer userId, String id, String email,
        String nickname, String profileImage, String status
) {
    public static MeResonseDto from(User u) {
        return new MeResonseDto(u.getUserId(), u.getId(), u.getEmail(),
                u.getNickname(), u.getProfileImage(), u.getStatus());
    }
}