package com.springboot.jwttest.post.model.repository;

import com.springboot.jwttest.post.model.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Integer> {
}
