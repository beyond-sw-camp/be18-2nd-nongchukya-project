package com.springboot.jwttest.jwt.auth.dto;

import com.springboot.jwttest.user.model.vo.User;

public record MeResonseDto(
        Integer userId, String id, String email,
        String nickname, String profileImage, String status
) {
    public static MeResonseDto from(User u) {
        return new MeResonseDto(u.getUserId(), u.getId(), u.getEmail(),
                u.getNickname(), u.getProfileImage(), u.getStatus());
    }
}
