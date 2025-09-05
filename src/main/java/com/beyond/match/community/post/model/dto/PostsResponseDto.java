package com.beyond.match.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
// 게시글 전체 조회시 사용하는 Dto
public class PostsResponseDto {
    private final String title;

//    댓글 수
//    private final int commentCount;

    private final String userNickname;

    private final LocalDateTime createdAt;

    private final int viewCount;
}