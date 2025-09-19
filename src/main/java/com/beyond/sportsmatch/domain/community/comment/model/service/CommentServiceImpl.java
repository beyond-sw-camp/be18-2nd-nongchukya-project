package com.beyond.sportsmatch.domain.community.comment.model.service;

import com.beyond.sportsmatch.common.exception.CommunityException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentRequestDto;
import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentResponseDto;
import com.beyond.sportsmatch.domain.community.comment.model.repository.CommentRepository;
import com.beyond.sportsmatch.domain.community.comment.model.entity.Comment;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public Comment save(User user, Post post, Comment parentComment, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new CommunityException(ExceptionMessage.COMMENT_CONTENT_BLANK);
        }

        if (parentComment != null && !commentRepository.existsById(parentComment.getCommentId())) {
            throw new CommunityException(ExceptionMessage.COMMENT_NOT_FOUND);
        }

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public Comment getCommentById(int commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommunityException(ExceptionMessage.COMMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseDto> getCommentsByPostId(int postId) {
        List<Comment> parentComments = commentRepository.findByPost_PostIdAndParentCommentIsNull(postId);

        return parentComments.stream()
                .map(this::convertToDtoWithReplies)
                .toList();
    }

    @Transactional
    @Override
    public CommentResponseDto updateComment(int commentId, CommentRequestDto commentRequestDto, User user) {
        Comment comment = getCommentById(commentId);

        // 작성자 검증
        if (comment.getUser().getUserId()!=(user.getUserId())) {
            throw new CommunityException(ExceptionMessage.NOT_COMMENT_CREATOR);
        }

        comment.setContent(commentRequestDto.getContent());

        return CommentResponseDto.from(comment);
    }

    @Transactional
    @Override
    public void deleteComment(int commentId, User user) {
        Comment comment = getCommentById(commentId);

        if (comment.getUser().getUserId()!=(user.getUserId())) {
            throw new CommunityException(ExceptionMessage.NOT_COMMENT_CREATOR);
        }

        // 자식 댓글이 있으면 모두 삭제 (cascade = ALL + orphanRemoval = true)
        commentRepository.delete(comment);
    }

    private CommentResponseDto convertToDtoWithReplies(Comment comment) {
        List<CommentResponseDto> replies = commentRepository.findByParentComment_CommentId(comment.getCommentId())
                .stream()
                .map(this::convertToDtoWithReplies)
                .toList();

        boolean isAuthor = (comment.getUser().getUserId()) == (comment.getPost().getUser().getUserId());

        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                replies,
                isAuthor
        );
    }
}
