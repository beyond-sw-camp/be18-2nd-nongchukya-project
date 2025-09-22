package com.beyond.sportsmatch.domain.notification.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification",
        indexes = @Index(name="idx_notif_user_unread", columnList="userId, readAt"))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;

    @Column(name = "user_id")
    private int userId;

    private String type;

    private String title;
    private String body;

    @Column(name = "match_id")
    private int matchId;
    @Column(name = "chat_room_id")
    private int chatRoomId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "post_id")
    private int postId;

    @Column(name="comment_id")
    private int commentId;
}
