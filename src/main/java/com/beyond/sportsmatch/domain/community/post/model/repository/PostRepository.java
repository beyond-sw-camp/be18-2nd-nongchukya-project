package com.beyond.sportsmatch.domain.community.post.model.repository;

import com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Integer> {

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.postId = :postId")
    Optional<Post> findByIdWithUser(@Param("postId") int postId);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.postId = :postId")
    void incrementViewCount(@Param("postId") int postId);

    @Query("SELECT new com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto(" +
            "p.postId, p.title, u.nickname, COUNT(DISTINCT c), COUNT(DISTINCT l), p.createdAt, p.viewCount) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.postLikes l " +
            "GROUP BY p.postId, p.title, u.nickname, p.createdAt, p.viewCount")
    Page<PostsResponseDto> findPostsWithCommentAndLikeCount(Pageable pageable);



    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.postId = :postId")
    Optional<Post> findByIdWithUserAndComments(@Param("postId") int postId);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +  // 최상위 댓글 fetch
            "LEFT JOIN FETCH c.replies r " +   // 대댓글 fetch
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH r.user " +
            "WHERE p.postId = :postId")
    Optional<Post> findByIdWithComments(@Param("postId") int postId);
}