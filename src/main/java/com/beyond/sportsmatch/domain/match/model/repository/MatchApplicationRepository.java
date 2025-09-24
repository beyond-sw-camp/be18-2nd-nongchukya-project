package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.match.model.entity.MatchStatus;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Repository
public interface MatchApplicationRepository extends JpaRepository<MatchApplication, Integer> {
    List<MatchApplication> findByMatchDate(LocalDate date);

    Page<MatchApplication> findByApplicantId(User applicantId, Pageable pageable);

    int countByApplicantId(User applicantId);

    List<MatchApplication> findByMatchDateBefore(LocalDate now);

    boolean existsByApplicantIdAndMatchDate(User applicant, LocalDate matchDate);

    Page<MatchApplication> findByApplicantIdAndStatusIn(User applicantId, Set<MatchStatus> allStatuses, Pageable pageable);

    int countByApplicantIdAndStatus(User applicantId, MatchStatus matchStatus);

    @Query("SELECT ma FROM MatchApplication ma WHERE ma.applicantId.userId IN :userIds AND ma.status = :status")
    List<MatchApplication> findByUserIdsAndStatus(@Param("userIds") List<Integer> userIds, @Param("status") MatchStatus status);

    List<MatchApplication> findByMatchDateBeforeAndStatus(LocalDate now, MatchStatus matchStatus);

    List<MatchApplication> findByMatchDateAndStartTimeBeforeAndStatus(LocalDate now, LocalTime now1, MatchStatus matchStatus);

    boolean existsByApplicantIdAndMatchDateAndStatusIn(User applicant, LocalDate matchDate, Set<MatchStatus> activeStatuses);
}
