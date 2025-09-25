package com.beyond.sportsmatch.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class UserResponseDto {
    private final int userId;

    private final String nickname;

    private final String profileImage;

}
