package com.beyond.match.community.post.model.dto;

import com.beyond.match.community.comment.model.dto.CommentResponseDto;
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

//    댓글, 좋아요, 첨부 파일, 태그 추가
    private final List<CommentResponseDto> comments;
}
