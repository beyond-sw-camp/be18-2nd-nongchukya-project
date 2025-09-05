package com.beyond.match.community.comment.model.service;

import com.beyond.match.community.comment.model.repository.CommentRepository;
import com.beyond.match.community.comment.model.vo.Comment;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.user.model.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public Comment save(User user, Post post, Comment parentComment, String content) {
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }
}
