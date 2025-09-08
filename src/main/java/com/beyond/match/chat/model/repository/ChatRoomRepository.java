package com.beyond.match.chat.model.repository;

import com.beyond.match.chat.model.vo.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    List<ChatRoom> findByIsGroupChat(String isGroupChat);
}
