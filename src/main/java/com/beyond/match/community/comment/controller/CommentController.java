package com.beyond.match.community.comment.controller;

import com.beyond.match.common.model.dto.BaseResponseDto;
import com.beyond.match.community.comment.model.dto.CommentRequestDto;
import com.beyond.match.community.comment.model.dto.CommentResponseDto;
import com.beyond.match.community.comment.model.service.CommentService;
import com.beyond.match.community.comment.model.vo.Comment;
import com.beyond.match.community.post.model.service.PostService;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.jwt.auth.model.security.UserDetailsImpl;
import com.beyond.match.user.model.vo.User;
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
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

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
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment parentComment = commentService.getCommentById(commentId);

        if (parentComment == null) {
            throw new RuntimeException("부모 댓글이 존재하지 않습니다.");
        }

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