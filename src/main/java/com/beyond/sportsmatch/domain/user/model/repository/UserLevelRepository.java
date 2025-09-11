package com.beyond.sportsmatch.domain.user.model.repository;



import com.beyond.sportsmatch.domain.match.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevel;
import com.beyond.sportsmatch.domain.user.model.entity.UserLevelId;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserLevelRepository extends JpaRepository<UserLevel, UserLevelId> {
    List<UserLevel> findByUser(User user);
    Optional<UserLevel> findByUserAndSport(User user, Sport sport);
    // 관심 운동만 가져오기 + Sport fetch join으로 LazyInitialization 문제 방지
    @Query("SELECT ul FROM UserLevel ul JOIN FETCH ul.sport WHERE ul.user = :user AND ul.interest = true")
    List<UserLevel> findInterestedByUser(@Param("user") User user);
}
