package com.beyond.match.community.post.controller;
/*
    - 게시글 작성
    POST api/v1/community/posts

    - 게시글 조회
    GET api/v1/community/posts

    - 게시글 상세 조회
    GET api/v1/community/posts/{postId}

    - 게시글 수정
    PUT api/v1/community/posts/{postId}

    - 게시글 삭제
    DELETE api/v1/community/posts/{postId}
 */

import com.beyond.match.common.model.dto.BaseResponseDto;
import com.beyond.match.common.model.dto.ItemsResponseDto;
import com.beyond.match.community.comment.model.service.CommentService;
import com.beyond.match.community.post.model.dto.PostRequestDto;
import com.beyond.match.community.post.model.dto.PostResponseDto;
import com.beyond.match.community.post.model.dto.PostsResponseDto;
import com.beyond.match.community.post.model.service.PostService;
import com.beyond.match.community.post.model.vo.Category;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.jwt.auth.model.security.UserDetailsImpl;
import com.beyond.match.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/posts")
    public ResponseEntity<ItemsResponseDto<PostsResponseDto>> getPosts(@RequestParam int page, @RequestParam int numOfRows) {
        int totalCount = postService.getTotalCount();
        List<PostsResponseDto> posts = postService.getPosts(page, numOfRows);

        if(!posts.isEmpty()){
            return ResponseEntity.ok(
                    new ItemsResponseDto<>(HttpStatus.OK, posts, page, totalCount)
            );
        }else{
            throw new RuntimeException("게시글이 없습니다.");
        }
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<BaseResponseDto<PostResponseDto>> getPost(@PathVariable int postId) {
        PostResponseDto postResponseDto = postService.getPost(postId);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, postResponseDto));
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(
            @RequestBody PostRequestDto postRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        Category category = postService.findCategoryById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .user(user)
                .category(category)
                .build();

        // DB 저장 후 생성된 PK값을 사용하기 위해 DB에 먼저 저장
        Post savedPost = postService.save(post);

        return ResponseEntity
                .created(URI.create("/api/v1/community/posts/" + savedPost.getPostId()))
                .body(savedPost);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<BaseResponseDto<PostResponseDto>> updatePost(
            @RequestBody PostRequestDto postRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int postId) {
        User user = userDetails.getUser();

        PostResponseDto updatedPostResponseDto = postService.updatePost(postRequestDto, user, postId);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, updatedPostResponseDto));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<BaseResponseDto<String>> deletePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int postId){
        User user = userDetails.getUser();

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

        if(!post.getUser().getId().equals(user.getId())){
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        postService.deletePost(postId);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "게시글이 삭제되었습니다."));
    }
}
