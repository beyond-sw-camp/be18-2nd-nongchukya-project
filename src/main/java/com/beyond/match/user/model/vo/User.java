package com.beyond.match.user.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;        // PK

    @Column(name="id", nullable=false, length=255)
    private String id;        // 로그인 ID

    @Column(nullable=false, length=255, unique = true)
    private String email;

    @Column(nullable=false, length=255)
    private String password;

    @Column(name="profile_image")
    private String profileImage;

    private String name;
    private String nickname;
    private String gender;
    private Integer age;
    private String address;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="dm_option")
    private Boolean dmOption;

    private String status;
}