package com.beyond.sportsmatch.domain.friend.model.entity;

import com.beyond.sportsmatch.domain.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friend_request")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private int RequestId;
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", nullable = false)
    private User senderUserId;
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", nullable = false)
    private User receiverUserId;
    @Column(name = "status")
    private String status;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
