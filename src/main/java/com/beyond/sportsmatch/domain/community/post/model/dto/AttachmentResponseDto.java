package com.beyond.sportsmatch.domain.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class AttachmentResponseDto {
    private final int attachmentId;

    private final String originalName;

    private final String fileUrl;
}
