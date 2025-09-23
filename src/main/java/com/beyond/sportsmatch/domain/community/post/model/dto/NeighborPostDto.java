package com.beyond.sportsmatch.domain.community.post.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class NeighborPostDto {
    private int postId;

    private String title;

    public NeighborPostDto(int postId, String title) {
        this.postId = postId;
        this.title = title;
    }
}
