package com.beyond.sportsmatch.domain.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostRequestDto {
    private final String title;

    private final String content;

    private final int categoryId;
}
