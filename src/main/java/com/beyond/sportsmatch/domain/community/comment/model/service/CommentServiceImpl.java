package com.beyond.sportsmatch.domain.community.comment.model.service;

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
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
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
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다. id=" + commentId));
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
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        comment.setContent(commentRequestDto.getContent());

        return CommentResponseDto.from(comment);
    }

    @Transactional
    @Override
    public void deleteComment(int commentId, User user) {
        Comment comment = getCommentById(commentId);

        if (comment.getUser().getUserId()!=(user.getUserId())) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        // 자식 댓글이 있으면 모두 삭제 (cascade = ALL + orphanRemoval = true)
        commentRepository.delete(comment);
    }

    private CommentResponseDto convertToDtoWithReplies(Comment comment) {
        List<CommentResponseDto> replies = commentRepository.findByParentComment_CommentId(comment.getCommentId())
                .stream()
                .map(this::convertToDtoWithReplies)
                .toList();

        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                replies
        );
    }
}
