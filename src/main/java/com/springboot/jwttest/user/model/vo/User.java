package com.springboot.jwttest.user.model.vo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;        // PK

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
