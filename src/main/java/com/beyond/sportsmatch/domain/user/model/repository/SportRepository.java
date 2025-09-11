package com.beyond.sportsmatch.domain.user.model.repository;



import com.beyond.sportsmatch.domain.match.model.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportRepository extends JpaRepository<Sport, Integer> {
    Optional<Sport> findByName(String name);
}