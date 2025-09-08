package com.beyond.match.community.comment.model.service;

import com.beyond.match.community.comment.model.dto.CommentRequestDto;
import com.beyond.match.community.comment.model.dto.CommentResponseDto;
import com.beyond.match.community.comment.model.vo.Comment;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.user.model.vo.User;

import java.util.Collection;
import java.util.List;

public interface CommentService {
    Comment save(User user, Post post, Comment parentComment, String content);

    Comment getCommentById(int commentId);

    List<CommentResponseDto> getCommentsByPostId(int postId);

    CommentResponseDto updateComment(int commentId, CommentRequestDto commentRequestDto, User user);

    void deleteComment(int commentId, User user);
}
