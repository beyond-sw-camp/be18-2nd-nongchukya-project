package com.beyond.sportsmatch.domain.notification.controller;

import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.dto.BaseResponseDto;
import com.beyond.sportsmatch.domain.notification.model.entity.Notification;
import com.beyond.sportsmatch.domain.notification.model.repository.NotificationRepository;
import com.beyond.sportsmatch.domain.notification.model.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<BaseResponseDto<Notification>> getNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Notification> notifications = notificationService.getNotifications(userDetails);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponseDto<Map<String, Integer>>> unreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        int count = notificationService.getUnreadCount(userDetails);
        Map<String, Integer> map = Map.of("count", count);
        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, map));
    }

//    @PostMapping("/{id}/read")
//    @Transactional
//    public void markRead(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
//        Notification n = notificationRepository.findById(id).orElseThrow();
//        if(n.getUserId()!=userDetails.getUser().getUserId()) {
//            throw new AccessDeniedException(("당신의 알림이 아닙니다."));
//        }
//        n.setReadAt(LocalDateTime.now());
//    }

    @PostMapping("/read-all")
    @Transactional
    public ResponseEntity<BaseResponseDto<Map<String, Integer>>> markAllRead(@AuthenticationPrincipal UserDetailsImpl me) {
        int updated = notificationService.readAll(me);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK,Map.of("updated", updated)));
    }

    @DeleteMapping("/{notificationId}")
    @Transactional
    public ResponseEntity<BaseResponseDto<String>> delete(@PathVariable int notificationId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {

        notificationService.deleteNotification(notificationId, userDetails);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "삭제가 완료되었습니다."));
    }
}
