package com.springboot.jwttest.post.model.service;

import com.springboot.jwttest.post.model.dto.PostRequestDto;
import com.springboot.jwttest.post.model.mappers.postMapper;
import com.springboot.jwttest.post.model.repository.PostRepository;
import com.springboot.jwttest.post.model.vo.Post;
import com.springboot.jwttest.user.model.repository.UserRepository;
import com.springboot.jwttest.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Post create(int currentUserId, PostRequestDto postPostRequestDto) {
        User user = userRepository.findById(currentUserId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Post post = new Post();
        post.setTitle(postPostRequestDto.getTitle());
        post.setContent(postPostRequestDto.getContent());
        post.setUser(user);

        return postRepository.save(post);
    }
}
