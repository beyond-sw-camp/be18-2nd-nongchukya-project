package com.beyond.match.community.post.model.repository;

import com.beyond.match.community.post.model.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Integer> {
}