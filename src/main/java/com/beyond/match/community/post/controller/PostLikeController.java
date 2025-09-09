package com.beyond.match.community.post.controller;

import com.beyond.match.community.post.model.service.PostLikeService;
import com.beyond.match.community.post.model.vo.PostLikeId;
import com.beyond.match.jwt.auth.model.security.UserDetailsImpl;
import com.beyond.match.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/community/posts")
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> postLike(
            @PathVariable("postId") int postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user =  userDetails.getUser();
        boolean liked = postLikeService.postLike(postId, user);

        if(liked){
            return ResponseEntity.ok("좋아요 성공");
        }else{
            return ResponseEntity.ok("좋아요 취소");
        }
    }

    @GetMapping("/{postId}/like")
    public ResponseEntity<String> isLiked(
            @PathVariable("postId") int postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user =  userDetails.getUser();
        boolean liked = postLikeService.isLiked(postId, user);

        if(liked){
            return ResponseEntity.ok("좋아요를 누른 상태입니다.");
        }else{
            return ResponseEntity.ok("좋아요를 누르지 않은 상태입니다.");
        }
    }
}
