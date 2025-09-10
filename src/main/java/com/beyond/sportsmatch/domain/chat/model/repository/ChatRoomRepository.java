package com.beyond.sportsmatch.domain.chat.model.repository;

import com.beyond.sportsmatch.domain.chat.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    List<ChatRoom> findByIsGroupChat(String isGroupChat);
}
