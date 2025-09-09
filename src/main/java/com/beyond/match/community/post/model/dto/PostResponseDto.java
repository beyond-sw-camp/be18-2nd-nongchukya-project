package com.beyond.match.community.post.model.dto;

import com.beyond.match.community.comment.model.dto.CommentResponseDto;
import com.beyond.match.community.post.model.vo.Attachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class PostResponseDto {
    private final String title;

    private final String userProfileImage;

    private final String userNickname;

    private final LocalDateTime updatedAt;

    // 수정된 적이 있는 게시글인지
    private final boolean isUpdated;

    private final int viewCount;

    private final String content;

    private final List<CommentResponseDto> comments;

    private final List<AttachmentResponseDto> attachments;

    private final int likeCount;

    private final boolean liked;
}
