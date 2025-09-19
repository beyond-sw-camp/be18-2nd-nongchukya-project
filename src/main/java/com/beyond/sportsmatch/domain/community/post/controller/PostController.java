package com.beyond.sportsmatch.domain.community.post.controller;
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

import com.beyond.sportsmatch.auth.model.service.UserDetailsImpl;
import com.beyond.sportsmatch.common.dto.BaseResponseDto;
import com.beyond.sportsmatch.common.dto.ItemsResponseDto;
import com.beyond.sportsmatch.common.exception.CommunityException;
import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import com.beyond.sportsmatch.domain.community.post.model.dto.AttachmentResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostRequestDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.PostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.SearchPostsResponseDto;
import com.beyond.sportsmatch.domain.community.post.model.dto.UpdatePostRequestDto;
import com.beyond.sportsmatch.domain.community.post.model.service.PostService;
import com.beyond.sportsmatch.domain.community.post.model.entity.Category;
import com.beyond.sportsmatch.domain.community.post.model.entity.Post;

import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<ItemsResponseDto<PostsResponseDto>> getPosts(
            @RequestParam int page,
            @RequestParam int numOfRows,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        int totalCount = postService.getTotalCount();
        List<PostsResponseDto> posts = postService.getPosts(page, numOfRows, sortBy, sortDir);

        if(!posts.isEmpty()){
            return ResponseEntity.ok(
                    new ItemsResponseDto<>(HttpStatus.OK, posts, page, totalCount)
            );
        }else{
            throw new CommunityException(ExceptionMessage.POST_NOT_FOUND);
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

        if (postRequestDto.getTitle() == null || postRequestDto.getTitle().trim().isEmpty()) {
            throw new CommunityException(ExceptionMessage.POST_TITLE_BLANK);
        }

        Category category = postService.findCategoryById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new CommunityException(ExceptionMessage.CATEGORY_NOT_FOUND));

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
                .orElseThrow(() -> new CommunityException(ExceptionMessage.POST_NOT_FOUND));

        if(post.getUser().getUserId()!=(user.getUserId())){
            throw new CommunityException(ExceptionMessage.NOT_POST_CREATOR);
        }

        postService.deletePost(postId);

        return ResponseEntity.ok(new BaseResponseDto<>(HttpStatus.OK, "게시글이 삭제되었습니다."));
    }

    @GetMapping("/posts/search")
    public ResponseEntity<ItemsResponseDto<SearchPostsResponseDto>> searchPosts(
            @RequestParam String type,
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int numOfRows,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        if (keyword == null || keyword.trim().isEmpty()) {

            throw new CommunityException(ExceptionMessage.SEARCH_KEYWORD_BLANK);
        }

        Page<SearchPostsResponseDto> postPage = postService.searchPosts(type, keyword, page, numOfRows, sortBy, sortDir);
        List<SearchPostsResponseDto> posts = postPage.getContent();

        if (postPage.isEmpty()) {
            throw new CommunityException(ExceptionMessage.POST_NOT_FOUND);
        }

        return ResponseEntity.ok(
                new ItemsResponseDto<>(
                        HttpStatus.OK,
                        posts,
                        page,
                        (int)postPage.getTotalElements()
                )
        );
    }
}
