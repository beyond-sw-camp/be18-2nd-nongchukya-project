package com.beyond.sportsmatch.domain.friend.model.repository;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendRequestDto;
import com.beyond.sportsmatch.domain.friend.model.entity.FriendRequest;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    @Query("SELECT new com.beyond.sportsmatch.domain.friend.model.dto.FriendRequestDto(u.nickname, u.profileImage, fr.createdAt) " +
            "FROM FriendRequest fr JOIN fr.senderUserId u " +
            "WHERE fr.receiverUserId.userId = :receiverUserId")
    List<FriendRequestDto> findReceivedFriendRequestsByUserId(int receiverUserId);

    @Query("SELECT new com.beyond.sportsmatch.domain.friend.model.dto.FriendRequestDto(u.nickname, u.profileImage, fr.createdAt) " +
            "FROM FriendRequest fr JOIN fr.receiverUserId u " +
            "WHERE fr.senderUserId.userId = :senderUserId")
    List<FriendRequestDto> findSentFriendRequestsByUserId(int senderUserId);

    @Query("DELETE FROM FriendRequest fr " +
            "WHERE fr.senderUserId.userId = :senderUserId " +
            "AND fr.receiverUserId.userId = :receiverUserId")
    @Modifying
    @Transactional
    void deleteBySenderUserIdAndReceiverUserId(int senderUserId, int receiverUserId);

    @Query("DELETE FROM FriendRequest fr " +
            "WHERE fr.receiverUserId.userId = :receiverUserId " +
            "AND fr.senderUserId.userId = :senderUserId")
    @Modifying
    @Transactional
    void deleteByReceiverUserIdAndSenderUserId(int receiverUserId, int senderUserId);

    @Modifying
    @Transactional
    boolean existsBySenderUserIdAndReceiverUserIdAndStatus(User sender, User receiver, String Pending);
}
