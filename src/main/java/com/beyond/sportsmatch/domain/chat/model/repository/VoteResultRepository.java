package com.beyond.sportsmatch.domain.chat.model.repository;

import com.beyond.sportsmatch.domain.chat.model.entity.Vote;
import com.beyond.sportsmatch.domain.chat.model.entity.VoteResult;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteResultRepository extends JpaRepository<VoteResult, Integer> {
    boolean existsByVoteAndUser(Vote vote, User user);

    List<VoteResult> findByVote(Vote vote);
}
