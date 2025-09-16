package com.beyond.sportsmatch.domain.user.model.repository;

import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByNickname(String nickname);

    @Query("select u.loginId from User u where u.userId = :userId")
    Optional<String> findLoginIdByUserId(@Param("userId") Integer userId);
}
