package com.beyond.match.community.post.model.service;

import com.beyond.match.community.post.model.repository.CategoryRepository;
import com.beyond.match.community.post.model.repository.PostRepository;
import com.beyond.match.community.post.model.vo.Category;
import com.beyond.match.community.post.model.vo.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public int getTotalCount() {

        return (int) postRepository.count();
    }

    @Override
    public List<Post> getPosts(int page, int numOfRows) {
        Pageable pageable = PageRequest.of(page - 1, numOfRows, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable).getContent();
    }

    @Override
    public Optional<Post> getPostById(int postId) {
        return postRepository.findById(postId);
    }

    @Override
    public Post save(Post post) {

        return postRepository.save(post);
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
