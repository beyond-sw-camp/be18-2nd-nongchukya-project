package com.beyond.sportsmatch.domain.community.comment.controller;

import com.beyond.sportsmatch.common.dto.BaseResponseDto;
import com.beyond.sportsmatch.common.exception.CommunityException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentRequestDto;
import com.beyond.sportsmatch.domain.community.comment.model.dto.CommentResponseDto;
import com.beyond.sportsmatch.domain.community.comment.model.service.CommentService;
import com.beyond.sportsmatch.domain.community.comment.model.entity.Comment;
import com.beyond.sportsmatch.domain.community.post.model.service.PostService;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;
import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    댓글 작성
    - POST /api/v1/community/posts/{postId}/comments

    댓글 수정
    - PUT /api/v1/community/posts/{postId}/comments/{commentsId}

    댓글 삭제
    - DELETE /api/v1/community/posts/{postId}/comments

    대댓글 작성
    - POST /api/v1/community/posts/{postId}/comments/{commentId}/replies

    대댓글 수정
    - PUT /api/v1/community/posts/{postId}/comments/{commentId}/replies/{replyId}

    대댓글 삭제
    - DELETE /api/v1/community/posts/{postId}/comments/{commentId}/replies/{replyId}
 */

@RestController
@RequestMapping("api/v1/community/posts/{postId}")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int postId){

        User user = userDetails.getUser();
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new CommunityException(ExceptionMessage.POST_NOT_FOUND));

        Comment comment = commentService.save(user, post, null, commentRequestDto.getContent());

        return ResponseEntity.ok(CommentResponseDto.from(comment));
    }

    @PostMapping("/comments/{commentId}/replies")
        public ResponseEntity<CommentResponseDto> createReply(
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int postId,
            @PathVariable int commentId){

        User user = userDetails.getUser();
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new CommunityException(ExceptionMessage.POST_NOT_FOUND));

        Comment parentComment = commentService.getCommentById(commentId);

        Comment reply = commentService.save(user, post, parentComment, commentRequestDto.getContent());

        return ResponseEntity.ok(CommentResponseDto.from(reply));
    }

    @PutMapping("comments/{commentId}")
    public ResponseEntity<BaseResponseDto<CommentResponseDto>> updateComment(
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int commentId){
        User user = userDetails.getUser();

        CommentResponseDto updatedCommentResponseDto = commentService.updateComment(commentId, commentRequestDto, user);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, updatedCommentResponseDto));
    }

    // 댓글/대댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<BaseResponseDto<String>> deleteComment(
            @PathVariable int commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        commentService.deleteComment(commentId, user);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "댓글이 삭제되었습니다."));
    }
}