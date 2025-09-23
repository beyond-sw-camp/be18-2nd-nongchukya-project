package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchCompletedRepository extends JpaRepository<MatchCompleted, Integer> {
    @Query("SELECT m FROM MatchCompleted m JOIN m.participants p WHERE p.userId = :userId")
    List<MatchCompleted> findAllByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MatchCompleted m JOIN m.participants p WHERE p.userId = :userId")
    int countByUserId(int userId);
}