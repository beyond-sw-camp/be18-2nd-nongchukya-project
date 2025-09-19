package com.beyond.sportsmatch.domain.friend.model.repository;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;
import com.beyond.sportsmatch.domain.friend.model.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend,Integer> {
    @Query("SELECT new com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto(u.nickname, u.profileImage, f.createdAt) " +
            "FROM Friend f JOIN f.friendUserId u " +
            "WHERE f.loginUserId.userId = :loginUserId")
    List<FriendResponseDto> findFriendsByUserId(@Param("loginUserId") int loginUserId);

    @Modifying
    @Transactional
    void deleteByLoginUserIdUserIdAndFriendUserIdUserId(int loginUserId, int friendUserId);

    Optional<Friend> findByLoginUserIdUserIdAndFriendUserIdUserId(int loginUserId, int friendUserId);

    boolean existsByLoginUserIdUserIdAndFriendUserIdUserId(int senderUserId, int receiverUserId);
}
