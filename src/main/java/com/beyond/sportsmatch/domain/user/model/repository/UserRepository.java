package com.beyond.sportsmatch.domain.user.model.repository;

import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(String login);

    User findByNickname(String nickname);
}