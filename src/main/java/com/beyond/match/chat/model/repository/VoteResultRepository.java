package com.beyond.match.chat.model.repository;

import com.beyond.match.chat.model.vo.Vote;
import com.beyond.match.chat.model.vo.VoteResult;
import com.beyond.match.user.model.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteResultRepository extends JpaRepository<VoteResult, Integer> {
    boolean existsByVoteAndUser(Vote vote, User user);

    List<VoteResult> findByVote(Vote vote);
}
