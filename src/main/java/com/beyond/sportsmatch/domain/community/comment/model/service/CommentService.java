package com.beyond.sportsmatch.domain.community.comment.model.service;

import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentRequestDto;
import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentResponseDto;
import com.beyond.sportsmatch.domain.community.comment.model.entity.Comment;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import com.beyond.sportsmatch.domain.user.model.entity.User;

import java.util.List;

public interface CommentService {
    Comment save(User user, Post post, Comment parentComment, String content);

    Comment getCommentById(int commentId);

    List<CommentResponseDto> getCommentsByPostId(int postId);

    CommentResponseDto updateComment(int commentId, CommentRequestDto commentRequestDto, User user);

    void deleteComment(int commentId, User user);
}
