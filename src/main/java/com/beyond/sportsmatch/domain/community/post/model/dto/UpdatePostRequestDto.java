package com.beyond.sportsmatch.domain.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdatePostRequestDto {
    private final String title;

    private final String content;

    private final int categoryId;

    private final List<Integer> deleteAttachmentIds;
}
