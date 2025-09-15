package com.beyond.sportsmatch.domain.user.model.repository;

import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportRepository extends JpaRepository<Sport, Integer> {
    Optional<Sport> findByName(String name);
}
