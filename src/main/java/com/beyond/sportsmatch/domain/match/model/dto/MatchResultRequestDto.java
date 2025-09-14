package com.beyond.sportsmatch.domain.match.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResultRequestDto {

    private String score;

    private String winner;

    private String resultNote;
}
