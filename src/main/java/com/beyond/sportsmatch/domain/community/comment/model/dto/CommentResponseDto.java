package com.beyond.sportsmatch.domain.community.comment.model.dto;

import com.beyond.sportsmatch.domain.community.comment.model.entity.Comment;
import lombok.Getter;
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

    private final boolean isAuthor; // 게시글 작성자인지 여부

    // 5인자 생성자
    public CommentResponseDto(int commentId, String userNickname, String content, LocalDateTime createdAt, List<CommentResponseDto> replies, boolean isAuthor) {
        this.commentId = commentId;
        this.userNickname = userNickname;
        this.content = content;
        this.createdAt = createdAt;
        this.replies = new ArrayList<>(replies); // 리스트 복사
        this.isAuthor = isAuthor;
    }

    // Comment -> DTO 변환
    public static CommentResponseDto from(Comment comment) {
        List<CommentResponseDto> replyDtos = comment.getReplies() == null
                ? List.of()
                : comment.getReplies().stream()
                .map(CommentResponseDto::from)
                .toList();

        boolean isAuthor = (comment.getUser().getUserId()) == (comment.getPost().getUser().getUserId());

        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                replyDtos,
                isAuthor
        );
    }
}

