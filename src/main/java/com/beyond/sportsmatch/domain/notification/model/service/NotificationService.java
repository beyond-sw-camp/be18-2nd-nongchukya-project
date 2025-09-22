package com.beyond.sportsmatch.domain.notification.model.service;

import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.exception.ChatException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.notification.model.entity.Notification;
import com.beyond.sportsmatch.domain.notification.model.repository.NotificationRepository;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationSseService notificationSseService;
    private final UserRepository userRepository;

    public List<Notification> getNotifications(UserDetailsImpl userDetails) {
        if(userDetails == null) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        int userId = userDetails.getUser().getUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public int getUnreadCount(UserDetailsImpl userDetails) {
        if(userDetails == null){
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        int userId = userDetails.getUser().getUserId();

        return notificationRepository.countByUserIdAndReadAtIsNull(userId);
    }

    public int readAll(UserDetailsImpl userDetails) {
        if(userDetails == null){
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        int userId = userDetails.getUser().getUserId();
        return notificationRepository.markAllReadByUserId(userId, LocalDateTime.now());
    }

    public void deleteNotification(int notificationId, UserDetailsImpl userDetails) {
        if(userDetails == null){
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        int userId = userDetails.getUser().getUserId();
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new ChatException(ExceptionMessage.NOTIFICATION_NOT_FOUND));

        if(userId!=notification.getUserId()){
            throw new ChatException(ExceptionMessage.FORBIDDEN_NOTIFICATION_DELETE);

        }
        notificationRepository.delete(notification);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMatchConfirmed(Integer matchId, Integer chatRoomId,
                                   String sport, String region, LocalDate date,
                                   String start, String end,
                                   List<Integer> userIds) {
        String title = "매칭 성사!";
        String body = "%s %s %s %s-%s 채팅방이 열렸어요."
                .formatted(sport, region, date, start, end);

        LocalDateTime now = LocalDateTime.now();

        List<Notification> rows = new ArrayList<>();
        for(Integer userId : userIds){
            rows.add(Notification.builder()
                    .userId(userId)
                    .type("MATCH_CONFIRMED")
                    .title(title)
                    .body(body)
                    .matchId(matchId)
                    .chatRoomId(chatRoomId)
                    .createdAt(now)
                    .build());
        }
        notificationRepository.saveAll(rows);

        Map<Integer, Integer> notifIdByUserId =
                rows.stream().collect(Collectors.toMap(Notification::getUserId, Notification::getNotificationId, (a, b)->b));
        for(Integer userId : userIds){
            String loginId = userRepository.findLoginIdByUserId(userId).orElseThrow(()->
                    new ChatException(ExceptionMessage.LOGINID_NOT_FOUND));
            if(loginId == null){
                continue;
            }
            Map<String, Object> payload = Map.of(
                    "id", notifIdByUserId.get(userId),
                    "type", "MATCH_CONFIRMED",
                    "matchId", matchId,
                    "chatRoomId", chatRoomId,
                    "title", title,
                    "body", body,
                    "createdAt", now.toString()
            );
            notificationSseService.send(loginId, "match-confirmed", payload);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMatchCancelled(Integer matchId, Integer chatRoomId,
                                   String sport, String region, LocalDate date,
                                   String start, String end,
                                   List<Integer> remainUserIds) {
        String title = "매칭이 취소되어 대기 중으로 돌아갔습니다.";
        String body = "%s %s %s %s-%s 매칭이 취소되었습니다."
                .formatted(sport, region, date, start, end);

        LocalDateTime now = LocalDateTime.now();

        List<Notification> rows = new ArrayList<>();
        for(Integer remainUserId : remainUserIds){
            rows.add(Notification.builder()
                    .userId(remainUserId)
                    .type("MATCH_CANCELLED")
                    .matchId(matchId)
                    .title(title)
                    .body(body)
                    .createdAt(now)
                    .build());
        }
        List<Notification> saved = notificationRepository.saveAll(rows);

        Map<Integer, Integer> notifIdByUserId =
                saved.stream().collect(Collectors.toMap(Notification::getUserId, Notification::getNotificationId, (a, b)->b));
        for(Integer remainUserId: remainUserIds){
            String loginId = userRepository.findLoginIdByUserId(remainUserId).orElseThrow(()->
                    new ChatException(ExceptionMessage.LOGINID_NOT_FOUND));
            if(loginId == null){
                continue;
            }
            Map<String, Object> payload = Map.of(
                    "id", notifIdByUserId.get(remainUserId),
                    "type", "MATCH_CANCELLED",
                    "title", title,
                    "body", body,
                    "matchId", matchId,
                    "createdAt", now.toString()
            );
            notificationSseService.send(loginId, "match-cancelled", payload);
        }
    }



}
