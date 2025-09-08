package com.beyond.match.chat.model.repository;

import com.beyond.match.chat.model.vo.ChatRoom;
import com.beyond.match.chat.model.vo.Vote;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VoteRepository extends CrudRepository<Vote, Integer> {
    List<Vote> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    List<Vote> findByChatRoom_ChatRoomId(int chatRoomId);
}
