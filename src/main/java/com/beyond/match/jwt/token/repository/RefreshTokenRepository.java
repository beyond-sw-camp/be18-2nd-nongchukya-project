package com.beyond.match.jwt.token.repository;

import com.beyond.match.jwt.token.vo.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Query("select r.tokenHash from RefreshToken r where r.userId = ?1")
    Optional<String> findHashByUserId(Integer userId);
}
