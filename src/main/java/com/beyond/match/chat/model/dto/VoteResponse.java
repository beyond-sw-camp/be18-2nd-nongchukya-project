package com.beyond.match.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteResponse {
    private int voteId;
    private String title;
    private String options; // JSON string 그대로
    private LocalDateTime createdAt;
}
