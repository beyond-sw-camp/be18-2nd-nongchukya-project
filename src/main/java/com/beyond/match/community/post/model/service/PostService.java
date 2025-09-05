package com.beyond.match.community.post.model.service;

import com.beyond.match.community.post.model.vo.Category;
import com.beyond.match.community.post.model.vo.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post save(Post post);

    Optional<Category> findCategoryById(int categoryId);

    int getTotalCount();

    List<Post> getPosts(int page, int numOfRows);

    Optional<Post> getPostById(int postId);

    void deletePost(int postId);
}
