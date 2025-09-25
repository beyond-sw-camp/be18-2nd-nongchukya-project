package com.beyond.sportsmatch.domain.chat.model.repository;

import com.beyond.sportsmatch.domain.chat.model.entity.ChatRoom;
import com.beyond.sportsmatch.domain.chat.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
}
