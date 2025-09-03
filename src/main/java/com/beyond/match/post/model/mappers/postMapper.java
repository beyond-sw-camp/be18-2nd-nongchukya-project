package com.beyond.match.post.model.mappers;

import com.beyond.match.post.model.vo.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface postMapper {
    int insert(Post post);
}
