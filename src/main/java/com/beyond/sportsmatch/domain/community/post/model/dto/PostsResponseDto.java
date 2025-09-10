package com.beyond.sportsmatch.domain.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
// 게시글 전체 조회시 사용하는 Dto
public class PostsResponseDto {
    private final int postId;

    private final String title;

    private final String userNickname;

    private final long commentCount;

    private final Long likeCount;

    private final LocalDateTime createdAt;

    private final int viewCount;
}