package com.beyond.sportsmatch.domain.chat.model.service;

import com.beyond.sportsmatch.common.exception.ChatException;
import com.beyond.sportsmatch.common.exception.SportsMatchException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.chat.model.dto.RoomDeletedEvent;
import com.beyond.sportsmatch.domain.chat.model.dto.UserListResDto;
import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.repository.MatchCompletedRepository;
import com.beyond.sportsmatch.domain.match.model.repository.MatchRepository;
import com.beyond.sportsmatch.domain.match.model.service.MatchRedisService;
import com.beyond.sportsmatch.domain.notification.model.service.NotificationService;
import com.beyond.sportsmatch.domain.user.model.repository.UserRepository;
import com.beyond.sportsmatch.domain.chat.model.dto.ChatDto;
import com.beyond.sportsmatch.domain.chat.model.dto.ChatRoomListResDto;
import com.beyond.sportsmatch.domain.chat.model.dto.MyChatListResDto;
import com.beyond.sportsmatch.domain.chat.model.repository.ChatMessageRepository;
import com.beyond.sportsmatch.domain.chat.model.repository.ChatParticipantRepository;
import com.beyond.sportsmatch.domain.chat.model.repository.ChatRoomRepository;
import com.beyond.sportsmatch.domain.chat.model.repository.ReadStatusRepository;
import com.beyond.sportsmatch.domain.chat.model.entity.ChatRoom;
import com.beyond.sportsmatch.domain.chat.model.entity.JoinedChatRoom;
import com.beyond.sportsmatch.domain.chat.model.entity.Message;
import com.beyond.sportsmatch.domain.chat.model.entity.MessageReadStatus;
import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final MatchRedisService matchRedisService;
    private final MatchRepository matchRepository;
    private final MatchCompletedRepository matchCompletedRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void saveMessage(int chatRoomId, ChatDto message) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()->
                new ChatException(ExceptionMessage.CHATROOM_NOT_FOUND));
        // 보낸 사람 조회
        User sender = userRepository.findByNickname(message.getSenderNickname()).orElseThrow(()->
                new ChatException(ExceptionMessage.USER_NOT_FOUND));
        if(sender == null){
            throw new ChatException(ExceptionMessage.USER_NOT_FOUND);
        }
        // 메세지 저장
        Message ms = Message.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(message.getMessage())
                .build();
        chatMessageRepository.save(ms);
        // 사용자별로 읽음여부 저장
        List<JoinedChatRoom>  joinedChatRooms = chatParticipantRepository.findByChatRoom(chatRoom);
        for (JoinedChatRoom joinedChatRoom : joinedChatRooms) {
            MessageReadStatus messageReadStatus = MessageReadStatus.builder()
                    .chatRoom(chatRoom)
                    .user(joinedChatRoom.getUser())
                    .message(ms)
                    .isRead(joinedChatRoom.getUser().equals(sender))
                    .build();
            readStatusRepository.save(messageReadStatus);
        }
    }

    public void createGroupRoom(String roomName) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByNickname(userDetails.getUser().getNickname())
                .orElseThrow(() -> new ChatException(ExceptionMessage.USER_NOT_FOUND));
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(roomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);
        // 채팅 참여자로 개설자 추가
        JoinedChatRoom joinedChatRoom = JoinedChatRoom.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(joinedChatRoom);
    }

    public List<ChatRoomListResDto> getGroupChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResDto> dtos = new ArrayList<>();
        for (ChatRoom c : chatRooms) {
            ChatRoomListResDto dto = ChatRoomListResDto.builder()
                    .roomId(c.getChatRoomId())
                    .roomName(c.getChatRoomName())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public void addParticipantToGroupChat(int roodId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByNickname(userDetails.getUser().getNickname())
                .orElseThrow(() -> new ChatException(ExceptionMessage.USER_NOT_FOUND));
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roodId).orElseThrow(()->
                new ChatException(ExceptionMessage.CHATROOM_NOT_FOUND));
        // 유저 조회
        if(chatRoom.getIsGroupChat().equals("N")){
            throw new ChatException(ExceptionMessage.CHATROOM_NOT_GROUP);
        }
        // 이미 참여자인지 검증
        Optional<JoinedChatRoom> participant = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user);
        if(!participant.isPresent()){
            addParticipantToRoom(chatRoom, user);
        }


    }
    // join 객체 생성 후 저장
    public void addParticipantToRoom(ChatRoom chatRoom, User user) {
        JoinedChatRoom chatParticipant = JoinedChatRoom.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatDto> getChatHistory(int roomId) {
        // 내가 해당 채팅방의 참여자가 아닐 경우 에러 발생
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new ChatException(ExceptionMessage.CHATROOM_NOT_FOUND));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByNickname(userDetails.getUser().getNickname())
                .orElseThrow(() -> new ChatException(ExceptionMessage.USER_NOT_FOUND));
        List<JoinedChatRoom> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        boolean check = false;
        for (JoinedChatRoom c : chatParticipants){
            if(c.getUser().equals(user)){
                check = true;
            }
        }
        if(!check){
            throw new ChatException(ExceptionMessage.NOT_CHAT_MEMBER);
        }
        // 특정 룸에 대한 message 조회
        List<Message> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
        List<ChatDto> dtos = new ArrayList<>();
        for (Message m : chatMessages) {
            ChatDto chatDto = ChatDto.builder()
                    .message(m.getContent())
                    .senderNickname(m.getUser().getNickname())
                    .build();
            dtos.add(chatDto);
        }
        return dtos;
    }

    public boolean isRoomParticipant(String nickname, int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new ChatException(ExceptionMessage.CHATROOM_NOT_FOUND));

        User user = userRepository.findByNickname(nickname).orElseThrow(()->
                new ChatException(ExceptionMessage.USER_NOT_FOUND));

        List<JoinedChatRoom> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (JoinedChatRoom c : chatParticipants){
            if(c.getUser().getUserId()==(user.getUserId())){
                return true;
            }
        }
        return false;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int messageRead(int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new ChatException(ExceptionMessage.CHATROOM_NOT_FOUND));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(()->
                new ChatException(ExceptionMessage.USER_NOT_FOUND));;
        int userId = user.getUserId();
        int updated = readStatusRepository.markAsReadIgnore(roomId, userId);
        // updated < 예상치 면 일부 행은 충돌로 스킵된 것. 다음 호출에서 다시 갱신될 수 있음.
        return updated;
    }

    public List<MyChatListResDto> getMyChatRooms() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(()->
                new ChatException(ExceptionMessage.USER_NOT_FOUND));;
        List<JoinedChatRoom> chatParticipants = chatParticipantRepository.findAllByUser(user);
        List<MyChatListResDto> dtos = new ArrayList<>();
        for (JoinedChatRoom c : chatParticipants){
            int count = readStatusRepository.countByChatRoomAndUserAndIsReadFalse(c.getChatRoom(), user);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getChatRoomId())
                    .roomName(c.getChatRoom().getChatRoomName())
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .unReadCount(count)
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public void leaveGroupChatRoom(int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new ChatException(ExceptionMessage.CHATROOM_NOT_FOUND));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(()->
                new ChatException(ExceptionMessage.USER_NOT_FOUND));;
        if(chatRoom.getIsGroupChat().equals("N")){
            throw new ChatException(ExceptionMessage.CHATROOM_NOT_GROUP);
        }
        JoinedChatRoom joinUser = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow(()->
                new ChatException(ExceptionMessage.USER_NOT_FOUND));
        MatchCompleted matchCompleted = matchCompletedRepository.findById(chatRoom.getMatchId()).orElseThrow(()->
                new ChatException(ExceptionMessage.COMPLETED_MATCH_NOT_FOUND));


        List<JoinedChatRoom> joins = chatParticipantRepository.findAllByChatRoom(chatRoom);
        List<Integer> remainUserIds = joins.stream().map(j -> j.getUser().getUserId()).filter(uid -> !uid.equals(user.getUserId())).toList();

        MatchApplication baseApp = matchRepository.findByApplicantIdAndMatchDate(
                userRepository.getReferenceById(remainUserIds.get(0)),
                matchCompleted.getMatchDate()
        );
        chatParticipantRepository.delete(joinUser);
        chatRoomRepository.delete(chatRoom);
        matchCompletedRepository.delete(matchCompleted);
        for (int remainUserId : remainUserIds) {
            addToMatchList(baseApp, remainUserId);
        }
        notificationService.sendMatchCancelled(
                matchCompleted.getMatchId(),
                roomId,
                matchCompleted.getSport().getName(),
                matchCompleted.getRegion(),
                matchCompleted.getMatchDate(),
                baseApp.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                baseApp.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                remainUserIds
        );
        applicationEventPublisher.publishEvent(new RoomDeletedEvent(roomId, "매칭이 취소되어 채팅방이 사라졌습니다."));
    }

    public int getOrCreatePrivateRoom(String otherNickname) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        var userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userDetails.getUser();
        User otherUser = userRepository.findByNickname(otherNickname).orElseThrow(()->
                new ChatException(ExceptionMessage.USER_NOT_FOUND));
        // 나와 상대방이 1:1채팅에 이미 참석하고 있다면 해당 roomId return
        Optional<ChatRoom> chatRoom = chatParticipantRepository.findExistingPrivateRoom(user.getUserId(), otherUser.getUserId());
        if(chatRoom.isPresent()){
            return chatRoom.get().getChatRoomId();
        }
        // 만약에 1:1채팅방이 없을경우 기존 채팅방 개설
        ChatRoom newRoom = ChatRoom.builder()
                .isGroupChat("N")
                .chatRoomName(user.getNickname() + "-" + otherUser.getNickname() + "의 채팅방")
                .build();
        chatRoomRepository.save(newRoom);
        // 두사람 모두 참여자로 새롭게 추가
        addParticipantToRoom(newRoom, user);
        addParticipantToRoom(newRoom, otherUser);

        return newRoom.getChatRoomId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer createRoomForMatch(int matchId, String roomName, List<Integer> userIds) {
        ChatRoom chatRoom = chatRoomRepository.findByMatchId(matchId).orElseGet(()->
                chatRoomRepository.saveAndFlush(
                        ChatRoom.builder()
                                .chatRoomName(roomName)
                                .isGroupChat("Y")
                                .matchId(matchId)
                                .build()
                ));

        Integer roomId = chatRoom.getChatRoomId();

        for (Integer uid : userIds) {
            boolean exists = chatParticipantRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(roomId, uid);

            if(!exists){
                JoinedChatRoom join = JoinedChatRoom.builder()
                        .chatRoom(chatRoom)
                        .user(userRepository.getReferenceById(uid))
                        .build();
                chatParticipantRepository.save(join);
            }
        }
        return roomId;
    }

    private String getMatchKey(MatchApplication dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        String formattedStartTime = dto.getStartTime().format(formatter);
        String formattedEndTime = dto.getEndTime().format(formatter);

        return String.format("match:%s:%s:%s:%s-%s",
                dto.getSport().getId(),
                dto.getRegion(),
                dto.getMatchDate(),
                formattedStartTime,
                formattedEndTime);
    }

    // key : 매칭조건, value : userId
    public void addToMatchList(MatchApplication matchApplication,int remainUserId) {
        String key = getMatchKey(matchApplication);
        String value = String.valueOf(remainUserId);
        long ttl = 7 * 24 * 60 * 60 * 1000L;
        long expireAt = System.currentTimeMillis() + ttl;
        matchRedisService.addToZSet(key, value, expireAt);
    }


    public void assertAccessible(int roomId, UserDetailsImpl userDetails) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() ->
                new SportsMatchException(ExceptionMessage.CHATROOM_NOT_FOUND));

        int userId = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(() ->
                new SportsMatchException(ExceptionMessage.USER_NOT_FOUND)).getUserId();

        boolean user = chatParticipantRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(roomId, userId);
        if(!user){ throw new SportsMatchException(ExceptionMessage.NOT_CHAT_MEMBER);}
    }

    public List<UserListResDto> getUsersNick(int roomId, UserDetailsImpl userDetails) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ChatException(ExceptionMessage.UNAUTHORIZED);
        }
        boolean user = chatParticipantRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(roomId,
                userDetails.getUser().getUserId());
        if(!user){ throw new SportsMatchException(ExceptionMessage.NOT_CHAT_MEMBER);}

        List<JoinedChatRoom> chatParticipants = chatParticipantRepository.findAllByChatRoom_ChatRoomId(roomId);
        List<UserListResDto> userListResDtos = new ArrayList<>();
        for(JoinedChatRoom joinedChatRoom : chatParticipants){
            userListResDtos.add(
                    UserListResDto.builder()
                            .userID(joinedChatRoom.getUser().getUserId())
                            .nickname(joinedChatRoom.getUser().getNickname())
                            .build()
            );
        }
        return userListResDtos;
    }
}
