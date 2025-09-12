package com.beyond.sportsmatch.domain.match.model.repository;

import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepo extends JpaRepository<MatchApplication, Integer> {
    List<MatchApplication> findByMatchDate(LocalDate date);
}
