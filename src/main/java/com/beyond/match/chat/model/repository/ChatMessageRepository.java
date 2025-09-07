package com.beyond.match.chat.model.repository;

import com.beyond.match.chat.model.vo.ChatRoom;
import com.beyond.match.chat.model.vo.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
}
