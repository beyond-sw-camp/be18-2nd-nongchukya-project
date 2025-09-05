package com.beyond.match.community.comment.controller;

import com.beyond.match.community.comment.model.dto.CommentRequestDto;
import com.beyond.match.community.comment.model.service.CommentService;
import com.beyond.match.community.comment.model.vo.Comment;
import com.beyond.match.community.post.model.service.PostService;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.jwt.auth.model.security.UserDetailsImpl;
import com.beyond.match.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public void createComment(
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int postId){

        User user = userDetails.getUser();
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException());

        Comment comment = commentService.save(user, post, null, commentRequestDto.getContent());
    }
}