package com.beyond.match.chat.model.repository;

import com.beyond.match.chat.model.vo.ChatRoom;
import com.beyond.match.chat.model.vo.MessageReadStatus;
import com.beyond.match.user.model.vo.User;
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
