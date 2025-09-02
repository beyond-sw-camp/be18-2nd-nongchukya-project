package com.springboot.jwttest.post.model.vo;

import com.springboot.jwttest.user.model.vo.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 제목
    private String title;
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
