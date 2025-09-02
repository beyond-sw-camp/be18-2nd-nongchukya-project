package com.springboot.jwttest.chat.model.vo;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;
    private int chatRoomId;
    private int senderId;
    private String content;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
