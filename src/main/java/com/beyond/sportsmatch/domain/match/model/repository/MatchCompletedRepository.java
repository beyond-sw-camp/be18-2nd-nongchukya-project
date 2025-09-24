package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchCompletedRepository extends JpaRepository<MatchCompleted, Integer> {
    @Query("SELECT m FROM MatchCompleted m JOIN m.participants p WHERE p.userId = :userId")
    List<MatchCompleted> findAllByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MatchCompleted m JOIN m.participants p WHERE p.userId = :userId")
    int countByUserId(int userId);

    /**
     * 특정 날짜(match_date)를 기준으로, 경기 결과(MatchResult)가 아직 등록되지 않은 매치를 찾습니다.
     * @param date 조회할 날짜
     * @return 결과가 없는 매치 목록
     */
    @Query("SELECT mc FROM MatchCompleted mc LEFT JOIN MatchResult mr ON mc.matchId = mr.match.matchId WHERE mr.id IS NULL AND mc.matchDate = :date")
    List<MatchCompleted> findMatchesByDateWithoutResult(@Param("date") LocalDate date);
}