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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friend_list")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id", nullable = false)
    private int friendId;
    @ManyToOne
    @JoinColumn(name = "login_user_id", referencedColumnName = "user_id", nullable = false)
    private User loginUserId;
    @ManyToOne
    @JoinColumn(name = "friend_user_id", referencedColumnName = "user_id", nullable = false)
    private User friendUserId;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}
