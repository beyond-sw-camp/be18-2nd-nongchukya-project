package com.beyond.match.post.controller;

import com.beyond.match.post.model.dto.PostRequestDto;
import com.beyond.match.post.model.service.PostService;
import com.beyond.match.post.model.vo.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;


    private int currentUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if(principal instanceof Integer uid){
            return uid;
        }
        return Integer.parseInt(principal.toString());
    }

    @PostMapping("/post")
    public ResponseEntity<Post> posts(@RequestBody PostRequestDto postPostRequestDto, Authentication auth) {
        Post savedPost = postService.create(currentUserId(auth), postPostRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

}
