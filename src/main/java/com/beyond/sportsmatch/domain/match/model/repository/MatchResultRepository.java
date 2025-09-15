package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Integer> {
    boolean existsByMatch(MatchCompleted matchCompleted);
}
