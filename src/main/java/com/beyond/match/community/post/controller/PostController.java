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
import com.beyond.match.community.post.model.dto.AttachmentResponseDto;
import com.beyond.match.community.post.model.dto.PostRequestDto;
import com.beyond.match.community.post.model.dto.PostResponseDto;
import com.beyond.match.community.post.model.dto.PostsResponseDto;
import com.beyond.match.community.post.model.dto.UpdatePostRequestDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

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
    public ResponseEntity<BaseResponseDto<PostResponseDto>> getPost(
            @PathVariable int postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        PostResponseDto postResponseDto = postService.getPost(postId, user);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, postResponseDto));
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("postRequestDto") PostRequestDto postRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        Category category = postService.findCategoryById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // DB 저장 후 생성된 PK값을 사용하기 위해 DB에 먼저 저장
        Post savedPost = postService.createPost(postRequestDto, files, user, category);

        List<AttachmentResponseDto> attachments = Optional.ofNullable(savedPost.getAttachments())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(att -> new AttachmentResponseDto(
                        att.getAttachmentId(),
                        att.getOriginalName(),
                        att.getFileUrl()))
                .toList();

        PostResponseDto postResponseDto = new PostResponseDto(
                savedPost.getTitle(),
                savedPost.getUser().getProfileImage(),
                savedPost.getUser().getNickname(),
                savedPost.getUpdatedAt(),
                savedPost.getUpdatedAt().isAfter(savedPost.getCreatedAt()),
                savedPost.getViewCount(),
                savedPost.getContent(),
                List.of(), // 댓글은 생성 직후 없으므로 빈 리스트
                attachments,
                0,
                false
        );

        return ResponseEntity
                .created(URI.create("/api/v1/community/posts/" + savedPost.getPostId()))
                .body(postResponseDto);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<BaseResponseDto<PostResponseDto>> updatePost(
            @RequestPart("updatePostRequestDto") UpdatePostRequestDto updatePostRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int postId) {
        User user = userDetails.getUser();

        PostResponseDto updatedPostResponseDto = postService.updatePost(updatePostRequestDto, files, user, postId);

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
