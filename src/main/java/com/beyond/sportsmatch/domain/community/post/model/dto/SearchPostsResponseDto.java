package com.beyond.sportsmatch.domain.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
public class SearchPostsResponseDto {
    private final int postId;
    private final String title;
    private final String content;
    private final String userNickname;
    private final long commentCount;
    private final Long likeCount;
    private final LocalDateTime createdAt;
    private final int viewCount;
}
