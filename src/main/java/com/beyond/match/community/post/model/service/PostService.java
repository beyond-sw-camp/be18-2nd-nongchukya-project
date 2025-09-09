package com.beyond.match.community.post.model.service;

import com.beyond.match.community.post.model.dto.PostRequestDto;
import com.beyond.match.community.post.model.dto.PostResponseDto;
import com.beyond.match.community.post.model.dto.PostsResponseDto;
import com.beyond.match.community.post.model.dto.UpdatePostRequestDto;
import com.beyond.match.community.post.model.vo.Category;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.user.model.vo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post save(Post post);

    Optional<Category> findCategoryById(int categoryId);

    int getTotalCount();

    List<PostsResponseDto> getPosts(int page, int numOfRows);

    Optional<Post> getPostById(int postId);

    PostResponseDto getPost(int postId, User user);

    void deletePost(int postId);

    PostResponseDto updatePost(UpdatePostRequestDto postRequestDto, List<MultipartFile> files, User user, int postId);

    Post createPost(PostRequestDto postRequestDto, List<MultipartFile> files, User user, Category category);
}
