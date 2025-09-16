package com.beyond.sportsmatch.domain.notification.controller;

import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.domain.notification.model.entity.Notification;
import com.beyond.sportsmatch.domain.notification.model.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @GetMapping
    public List<Notification> getNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(
                userDetails.getUser().getUserId()
        );
    }

    @GetMapping("/unread-count")
    public Map<String, Integer> unreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int count = notificationRepository.countByUserIdAndReadAtIsNull(
                userDetails.getUser().getUserId()
        );
        return Map.of("count", count);
    }

    @PostMapping("/{id}/read")
    @Transactional
    public void markRead(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        Notification n = notificationRepository.findById(id).orElseThrow();
        if(n.getUserId()!=userDetails.getUser().getUserId()) {
            throw new AccessDeniedException(("당신의 알림이 아닙니다."));
        }
        n.setReadAt(LocalDateTime.now());
    }

    @PostMapping("/read-all")
    @Transactional
    public Map<String, Integer> markAllRead(@AuthenticationPrincipal UserDetailsImpl me) {
        int updated = notificationRepository.markAllReadByUserId(me.getUser().getUserId(), LocalDateTime.now());
        return Map.of("updated", updated);
    }

    @DeleteMapping("/{notificationId}")
    @Transactional
    public void delete(@PathVariable int notificationId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        Notification n = notificationRepository.findById(notificationId).orElseThrow(()->
                new EntityNotFoundException("알림 없음"));
        if(n.getUserId()!=userDetails.getUser().getUserId()) {
            throw new AccessDeniedException("내 알림만 삭제할 수 있습니다");
        }
        notificationRepository.delete(n);
    }
}
