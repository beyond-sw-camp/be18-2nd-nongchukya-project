package com.beyond.sportsmatch.domain.user.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // JPA 에서 기본키를 자동으로 생성하는 방법
    // 1. @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 2. @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false, unique = true, length = 30)
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+=.-]{6,50}$")
    private String loginId;

    @Email
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable=false, unique = true)
    private String nickname;

    private String gender;

    private Integer age;

    private String address;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="dm_option")
    private Boolean dmOption;

    private String status;

    private String profileImage;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 비밀번호 변경
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 회원 탈퇴
    public void deactivate() {
        this.status = "N"; // 탈퇴 상태
    }
}