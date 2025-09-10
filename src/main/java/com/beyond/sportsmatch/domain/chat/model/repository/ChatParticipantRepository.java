package com.beyond.sportsmatch.domain.chat.model.repository;

import com.beyond.sportsmatch.domain.chat.model.entity.ChatRoom;
import com.beyond.sportsmatch.domain.chat.model.entity.JoinedChatRoom;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<JoinedChatRoom, Integer> {
    List<JoinedChatRoom> findByChatRoom(ChatRoom chatRoom);
    Optional<JoinedChatRoom> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<JoinedChatRoom> findAllByUser(User user);

    @Query("SELECT cp1.chatRoom FROM JoinedChatRoom cp1 JOIN JoinedChatRoom cp2 ON cp1.chatRoom.chatRoomId = cp2.chatRoom.chatRoomId where cp1.user.userId = :myId and cp2.user.userId = :otherUserId and cp1.chatRoom.isGroupChat = 'N'")
    Optional<ChatRoom> findExistingPrivateRoom(@Param("myId") int myId, @Param("otherUserId") int otherUserId);
}
