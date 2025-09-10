package com.beyond.sportsmatch.domain.chat.model.repository;

import com.beyond.sportsmatch.domain.chat.model.entity.ChatRoom;
import com.beyond.sportsmatch.domain.chat.model.entity.Vote;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VoteRepository extends CrudRepository<Vote, Integer> {
    List<Vote> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    List<Vote> findByChatRoom_ChatRoomId(int chatRoomId);
}
