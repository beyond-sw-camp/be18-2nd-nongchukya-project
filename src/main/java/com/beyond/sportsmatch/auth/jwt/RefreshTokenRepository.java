package com.beyond.sportsmatch.auth.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Query("select r.tokenHash from RefreshToken r where r.userId = ?1")
    Optional<String> findHashByUserId(Integer userId);
}