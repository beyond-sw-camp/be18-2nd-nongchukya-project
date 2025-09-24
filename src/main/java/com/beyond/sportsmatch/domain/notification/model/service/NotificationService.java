package com.beyond.sportsmatch.domain.notification.model.service;

import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.dto.ItemsResponseDto;
import com.beyond.sportsmatch.common.exception.ChatException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.community.comment.model.repository.CommentRepository;
import com.beyond.sportsmatch.domain.community.post.model.repository.PostRepository;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.repository.MatchCompletedRepository;
import com.beyond.sportsmatch.domain.friend.model.repository.FriendRequestRepository;
import com.beyond.sportsmatch.domain.notification.model.entity.Notification;
import com.beyond.sportsmatch.domain.notification.model.repository.NotificationRepository;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MatchCompletedRepository matchCompletedRepository;
    private final FriendRequestRepository friendRequestRepository;

    public ItemsResponseDto<Notification> getNotifications(UserDetailsImpl userDetails, int page, int size) {
        if(userDetails == null) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));
        int userId = userDetails.getUser().getUserId();
        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> p = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return new ItemsResponseDto<>(
                HttpStatus.OK,
                p.getContent(),
                p.getNumber(),
                (int) p.getTotalElements()
        );
    }

    public int getUnreadCount(UserDetailsImpl userDetails) {
        if(userDetails == null){
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        int userId = userDetails.getUser().getUserId();

        return notificationRepository.countByUserIdAndReadAtIsNull(userId);
    }

    @Transactional
    public int readAll(UserDetailsImpl userDetails) {
        if(userDetails == null){
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        int userId = userDetails.getUser().getUserId();
        return notificationRepository.markAllReadByUserId(userId, LocalDateTime.now());
    }

    @Transactional
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMatchEnded(MatchCompleted match) {
        String title = "경기 결과를 등록해주세요!";
        String body = "%s %s에서 진행된 경기의 결과를 등록하세요!"
                .formatted(
                        match.getMatchDate().toString(),
                        match.getRegion()
                );

        LocalDateTime now = LocalDateTime.now();
        List<Integer> userIds = match.getParticipants().stream().map(User::getUserId).toList();

        List<Notification> rows = new ArrayList<>();
        for(Integer userId : userIds){
            rows.add(Notification.builder()
                    .userId(userId)
                    .type("REQUEST_MATCH_RESULT")
                    .title(title)
                    .body(body)
                    .matchId(match.getMatchId())
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
                    "type", "REQUEST_MATCH_RESULT",
                    "matchId", match.getMatchId(),
                    "title", title,
                    "body", body,
                    "createdAt", now.toString()
            );
            notificationSseService.send(loginId, "request-match-result", payload);
        }
    }

    // 게시글 좋아요
    @Transactional
    public void notifyPostLiked(int postOwnerId, int postId, String likerNickname) {
        Notification notif = Notification.builder()
                .userId(postOwnerId)
                .type("LIKE")
                .title("게시글 좋아요")
                .body(likerNickname + "님이 회원님의 게시글을 좋아합니다.")
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> payload = Map.of(
                "id", notif.getPostId(),
                "type", notif.getType(),
                "title", notif.getTitle(),
                "body", notif.getBody(),
                "createdAt", now.toString()
        );
        notificationRepository.save(notif);
        notificationSseService.send(postRepository.findLoginIdByUserId(postOwnerId).orElseThrow(), "toggle-like", payload);
    }

    // 게시글 댓글
    @Transactional
    public void notifyPostCommented(int postOwnerId, int postId, int commentId, String commenterNickname) {
        Notification notif = Notification.builder()
                .userId(postOwnerId)
                .type("COMMENT")
                .title("게시글 댓글")
                .body(commenterNickname + "님이 회원님의 게시글에 댓글을 남겼습니다.")
                .postId(postId)
                .commentId(commentId)
                .createdAt(LocalDateTime.now())
                .build();

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> payload = Map.of(
                "id", notif.getPostId(),
                "type", notif.getType(),
                "title", notif.getTitle(),
                "body", notif.getBody(),
                "createdAt", now.toString()
        );
        notificationRepository.save(notif);
        notificationSseService.send(postRepository.findLoginIdByUserId(postOwnerId).orElseThrow(), "post-commented", payload);

    }

    // 댓글 답글
    @Transactional
    public void notifyCommentReplied(int commentOwnerId, int postId, int replyId, String replierNickname) {
        Notification notif = Notification.builder()
                .userId(commentOwnerId)
                .type("REPLY")
                .title("댓글 답글")
                .body(replierNickname + "님이 회원님의 댓글에 답글을 남겼습니다.")
                .postId(postId)
                .commentId(replyId)
                .createdAt(LocalDateTime.now())
                .build();

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> payload = Map.of(
                "id", notif.getPostId(),
                "type", notif.getType(),
                "title", notif.getTitle(),
                "body", notif.getBody(),
                "createdAt", now.toString()
        );
        notificationRepository.save(notif);
        notificationSseService.send(commentRepository.findLoginIdByCommentId(commentOwnerId).orElseThrow(), "comment-replied", payload);
    }

    @Transactional
    public void notifyReceivedFriendRequest(Integer senderUserId, Integer receiverId, String senderNickname) {
        Notification notif = Notification.builder()
                .userId(receiverId)
                .type("FRIEND_REQUEST")
                .title("친구 요청")
                .body(senderNickname + "님이 회원님에게 친구 요청을 보냈습니다.")
                .senderUserId(senderUserId)
                .createdAt(LocalDateTime.now())
                .build();

        notif = notificationRepository.save(notif);

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> payload = Map.of(
                "id", notif.getNotificationId(),
                "type", notif.getType(),
                "title", notif.getTitle(),
                "body", notif.getBody(),
                "createdAt", notif.getCreatedAt().toString()
        );
        notificationSseService.send(friendRequestRepository.findLoginIdByReceiverId(receiverId).orElseThrow(), "friend-request", payload);
    }
}
