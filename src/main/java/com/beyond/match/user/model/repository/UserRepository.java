package com.beyond.match.user.model.repository;

import com.beyond.match.user.model.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(String login);
}