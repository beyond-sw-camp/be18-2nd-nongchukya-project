package com.beyond.sportsmatch.domain.friend.model.repository;


import com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto;
import com.beyond.sportsmatch.domain.friend.model.entity.FriendRequest;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    @Query("SELECT new com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto(u.userId, u.nickname, u.profileImage, fr.createdAt) " +
            "FROM FriendRequest fr JOIN fr.senderUserId u " +
            "WHERE fr.receiverUserId.userId = :receiverUserId")
    List<FriendResponseDto> findReceivedFriendRequestsByUserId(int receiverUserId);

    @Query("SELECT new com.beyond.sportsmatch.domain.friend.model.dto.FriendResponseDto(u.userId, u.nickname, u.profileImage, fr.createdAt) " +
            "FROM FriendRequest fr JOIN fr.receiverUserId u " +
            "WHERE fr.senderUserId.userId = :senderUserId")
    List<FriendResponseDto> findSentFriendRequestsByUserId(int senderUserId);

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

    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END " +
            "FROM FriendRequest fr " +
            "WHERE fr.senderUserId.userId = :senderUserId " +
            "AND fr.receiverUserId.userId = :receiverUserId")
    boolean existsBySenderUserIdAndReceiverUserId(@Param("senderUserId") int senderUserId, @Param("receiverUserId") int receiverUserId);

    @Query("SELECT fr.receiverUserId.userId FROM FriendRequest fr WHERE fr.senderUserId.userId = :senderUserId")
    List<Integer> findReceiverIdsBySenderId(int senderUserId);

    @Query("select fr.senderUserId.userId from FriendRequest fr where fr.receiverUserId.userId = :receiverUserId")
    List<Integer> findSenderIdsByReceiverId(int receiverUserId);
    @Query("select u.loginId from User u where u.userId = :userId")
    Optional<String> findLoginIdByReceiverId(@Param("userId") int receiverId);
}
