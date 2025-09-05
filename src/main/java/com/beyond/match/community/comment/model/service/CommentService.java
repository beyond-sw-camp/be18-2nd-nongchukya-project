package com.beyond.match.community.comment.model.service;

import com.beyond.match.community.comment.model.vo.Comment;
import com.beyond.match.community.post.model.vo.Post;
import com.beyond.match.user.model.vo.User;

public interface CommentService {
    Comment save(User user, Post post, Comment parentComment, String content);
}
