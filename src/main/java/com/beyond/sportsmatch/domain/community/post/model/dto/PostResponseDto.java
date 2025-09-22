package com.beyond.sportsmatch.domain.community.post.model.dto;

import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private final boolean isAuthor;

    // Projection 전용 생성자
    public PostResponseDto(String title,
                           String userProfileImage,
                           String userNickname,
                           LocalDateTime updatedAt,
                           boolean isUpdated,
                           int viewCount,
                           String content,
                           long likeCount,   // ← Long/long 로 변경 추천
                           boolean liked,
                           boolean isAuthor) {
        this.title = title;
        this.userProfileImage = userProfileImage;
        this.userNickname = userNickname;
        this.updatedAt = updatedAt;
        this.isUpdated = isUpdated;
        this.viewCount = viewCount;
        this.content = content;
        this.comments = null;
        this.attachments = null;
        this.likeCount = (int) likeCount; // 캐스팅
        this.liked = liked;
        this.isAuthor = isAuthor;
    }
}
