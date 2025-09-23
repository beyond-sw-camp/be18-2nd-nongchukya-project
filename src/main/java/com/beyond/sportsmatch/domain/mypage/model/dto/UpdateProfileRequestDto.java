package com.beyond.sportsmatch.domain.mypage.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDto {

    private String name;
    private String nickname;
    private String phoneNumber;
    private String address;
    private String email;
    private Integer age;
    // 필요한 경우 프로필 이미지 URL 등 추가 가능
    private String profileImage;
}
