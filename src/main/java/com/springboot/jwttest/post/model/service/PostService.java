package com.springboot.jwttest.post.model.service;

import com.springboot.jwttest.post.model.dto.PostRequestDto;
import com.springboot.jwttest.post.model.vo.Post;

public interface PostService {
    Post create(int currentUserId, PostRequestDto postPostRequestDto);
}
