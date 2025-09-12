package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchCompletedRepo extends JpaRepository<MatchCompleted, Integer> {
}