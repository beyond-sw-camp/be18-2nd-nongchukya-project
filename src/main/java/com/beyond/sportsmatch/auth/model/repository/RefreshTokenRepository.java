package com.beyond.sportsmatch.auth.model.repository;


import com.beyond.sportsmatch.auth.model.entity.RefreshToken;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByToken(String refreshTokenValue);
}
