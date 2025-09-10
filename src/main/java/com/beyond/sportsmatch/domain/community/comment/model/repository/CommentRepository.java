package com.beyond.sportsmatch.domain.community.comment.model.repository;

import com.beyond.sportsmatch.domain.community.comment.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 루트 댓글 + 대댓글 재귀 조회
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.replies r " +
            "LEFT JOIN FETCH r.user " +
            "WHERE c.post.postId = :postId AND c.parentComment IS NULL")
    List<Comment> findAllRootCommentsWithReplies(@Param("postId") int postId);

    // 부모 댓글만 조회
    List<Comment> findByPost_PostIdAndParentCommentIsNull(int postId);

    // 특정 댓글의 자식 댓글 조회
    List<Comment> findByParentComment_CommentId(int parentId);

}
