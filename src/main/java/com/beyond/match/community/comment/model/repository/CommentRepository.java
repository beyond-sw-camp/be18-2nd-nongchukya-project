package com.beyond.match.community.comment.model.repository;

import com.beyond.match.community.comment.model.vo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
