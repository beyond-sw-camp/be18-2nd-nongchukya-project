package com.beyond.match.community.comment.model.dto;

import com.beyond.match.community.comment.model.vo.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
//@RequiredArgsConstructor
public class CommentResponseDto {
    private final int commentId;

    private final String userNickname;

    private final String content;

    private final LocalDateTime createdAt;

    private final List<CommentResponseDto> replies;

    // 5인자 생성자
    public CommentResponseDto(int commentId, String userNickname, String content, LocalDateTime createdAt, List<CommentResponseDto> replies) {
        this.commentId = commentId;
        this.userNickname = userNickname;
        this.content = content;
        this.createdAt = createdAt;
        this.replies = new ArrayList<>(replies); // 리스트 복사
    }

    // Comment -> DTO 변환
    public static CommentResponseDto from(Comment comment) {
        List<CommentResponseDto> replyDtos = comment.getReplies() == null
                ? List.of()
                : comment.getReplies().stream()
                .map(CommentResponseDto::from)
                .toList();
        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                replyDtos
        );
    }
}

