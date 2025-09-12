package com.beyond.sportsmatch.domain.mypage.model.dto;

import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProfileResponseDto {

    private String loginId;
    private String email;
    private String name;
    private String nickname;
    private String gender;
    private Integer age;
    private String address;
    private String phoneNumber;
    private Boolean dmOption;
    private String status;
    private String profileImage;

    private List<String> favoriteSports; // 관심 운동 이름 리스트

    public static ProfileResponseDto from(User user, List<UserLevel> userLevels) {
        List<String> favorites = userLevels.stream()
                .map(ul -> ul.getSport().getName()) // 관심 운동만 가져옴
                .collect(Collectors.toList());

        return new ProfileResponseDto(
                user.getLoginId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getGender(),
                user.getAge(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getDmOption(),
                user.getStatus(),
                user.getProfileImage(),
                favorites
        );
    }
}