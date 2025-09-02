package com.springboot.jwttest.post.model.mappers;

import com.springboot.jwttest.post.model.vo.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface postMapper {
    int insert(Post post);
}
