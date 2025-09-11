package com.beyond.sportsmatch.domain.user.model.repository;



import com.beyond.sportsmatch.domain.match.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevel;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLevelRepository extends JpaRepository<UserLevel, UserLevelId> {
    List<UserLevel> findByUser(User user);
    Optional<UserLevel> findByUserAndSport(User user, Sport sport);

}
