package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchApplication, Integer> {
    List<MatchApplication> findByMatchDate(LocalDate date);

    Page<MatchApplication> findByApplicantId(User applicantId, Pageable pageable);

    int countByApplicantId(User applicantId);
}
