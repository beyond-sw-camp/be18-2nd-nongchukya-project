package com.beyond.sportsmatch.domain.chat.model.repository;

import com.beyond.sportsmatch.domain.chat.model.entity.ChatRoom;
import com.beyond.sportsmatch.domain.chat.model.entity.MessageReadStatus;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadStatusRepository extends JpaRepository<MessageReadStatus, Integer> {
    List<MessageReadStatus> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    int countByChatRoomAndUserAndIsReadFalse(ChatRoom chatRoom, User user);

    @Modifying
    @Query(value = """
        UPDATE IGNORE message_read_status
        SET is_read = 1
        WHERE chat_room_id = :roomId
          AND user_id = :userId
          AND is_read = 0
    """, nativeQuery = true)
    int markAsReadIgnore(@Param("roomId") int roomId, @Param("userId") int userId);
}
