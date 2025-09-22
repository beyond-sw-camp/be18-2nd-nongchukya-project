package com.beyond.sportsmatch.domain.notification.model.repository;

import com.beyond.sportsmatch.domain.notification.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(int userId);
    Page<Notification> findByUserIdOrderByCreatedAtDesc(int userId, Pageable pageable);

    int countByUserIdAndReadAtIsNull(int userId);

    @Modifying
    @Query("update Notification n set n.readAt = :now where n.userId = :uid and n.readAt is null")
    int markAllReadByUserId(@Param("uid") Integer uid, @Param("now") LocalDateTime now);
}
