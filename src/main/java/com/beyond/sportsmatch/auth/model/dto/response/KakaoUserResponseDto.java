package com.beyond.sportsmatch.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // 모르는 필드 무시
public class KakaoUserResponseDto {

    private Long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true) // 모든 추가 필드 무시
    public static class KakaoAccount {
        @JsonProperty("has_email")
        private boolean hasEmail;

        @JsonProperty("email_needs_agreement")
        private boolean emailNeedsAgreement;

        @JsonProperty("is_email_valid")
        private boolean emailValid;

        @JsonProperty("is_email_verified")
        private boolean emailVerified;

        @JsonProperty("profile_nickname_needs_agreement")
        private boolean profileNicknameNeedsAgreement;

        @JsonProperty("profile_image_needs_agreement")
        private boolean profileImageNeedsAgreement;

        private String email;
        private Profile profile;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String nickname;

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;

        @JsonProperty("is_default_image")
        private boolean defaultImage;

        @JsonProperty("is_default_nickname")
        private boolean defaultNickname;
    }
}
