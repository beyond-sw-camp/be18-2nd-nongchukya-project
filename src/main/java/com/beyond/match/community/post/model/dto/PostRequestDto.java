package com.beyond.match.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class PostRequestDto {
    private String title;

    private String content;

    private int categoryId;
}
