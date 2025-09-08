package com.beyond.match.community.post.model.service;

import com.beyond.match.community.comment.model.dto.CommentResponseDto;
import com.beyond.match.community.comment.model.service.CommentService;
import com.beyond.match.community.post.model.dto.PostRequestDto;
import com.beyond.match.community.post.model.dto.PostResponseDto;
import com.beyond.match.community.post.model.dto.PostsResponseDto;
import com.beyond.match.community.post.model.repository.CategoryRepository;
import com.beyond.match.community.post.model.repository.PostRepository;
import com.beyond.match.community.post.model.vo.Category;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommentService commentService;

    @Override
    public int getTotalCount() {

        return (int) postRepository.count();
    }

    @Override
    public List<PostsResponseDto> getPosts(int page, int numOfRows) {
        Pageable pageable = PageRequest.of(page - 1, numOfRows, Sort.by("createdAt").descending());

        return postRepository.findPostsWithCommentCount(pageable).getContent();
    }

    @Override
    public Optional<Post> getPostById(int postId) {

        return postRepository.findByIdWithUser(postId);
    }

    @Transactional
    @Override
    public PostResponseDto getPost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);

        return new PostResponseDto(
                post.getTitle(),
                post.getUser().getProfileImage(),
                post.getUser().getNickname(),
                post.getUpdatedAt(),
                post.getUpdatedAt().isAfter(post.getCreatedAt()),
                post.getViewCount(),
                post.getContent(),
                comments
        );
    }

    @Override
    public Post save(Post post) {

        return postRepository.save(post);
    }

    @Transactional
    @Override
    public PostResponseDto updatePost(PostRequestDto postRequestDto, User user, int postId) {
        Post post = postRepository.findByIdWithUserAndComments(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        Category category = categoryRepository.findById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setCategory(category);

        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);

        postRepository.save(post); // DB 반영

        return new PostResponseDto(
                post.getTitle(),
                post.getUser().getProfileImage(),
                post.getUser().getNickname(),
                post.getUpdatedAt(),
                post.getUpdatedAt().isAfter(post.getCreatedAt()),
                post.getViewCount(),
                post.getContent(),
                comments
        );
    }

    @Override
    public void deletePost(int postId) {

        postRepository.deleteById(postId);
    }


    @Override
    public Optional<Category> findCategoryById(int categoryId) {

        return categoryRepository.findById(categoryId);
    }
}
