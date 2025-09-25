package com.beyond.sportsmatch.domain.community.comment.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CommentRequestDto {
    private final String content;

    private final int parentCommentId;
}
