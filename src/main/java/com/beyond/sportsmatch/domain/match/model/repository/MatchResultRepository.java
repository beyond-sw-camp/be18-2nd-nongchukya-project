package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.entity.MatchResult;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Integer> {
    boolean existsByMatch(MatchCompleted matchCompleted);

    @Query("SELECT mr FROM MatchResult mr JOIN mr.match.participants p WHERE p = :user")
    List<MatchResult> findMatchResultsByUser(@Param("user") User user);

    int countByUserId(int userId);
}
