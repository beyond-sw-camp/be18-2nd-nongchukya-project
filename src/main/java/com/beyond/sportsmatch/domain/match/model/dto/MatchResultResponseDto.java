package com.beyond.sportsmatch.domain.match.model.dto;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.entity.MatchResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultResponseDto {
    private int id;

    private CompletedMatchResponseDto match;

    private String score;

    private String winner;

    private String resultNote;

    public static MatchResultResponseDto fromEntity(MatchResult matchResult) {
        MatchResultResponseDto dto = new MatchResultResponseDto();
        dto.setId(matchResult.getId());
        dto.setMatch(CompletedMatchResponseDto.fromEntity(matchResult.getMatch()));
        dto.setScore(matchResult.getScore());
        dto.setWinner(matchResult.getWinner());
        dto.setResultNote(matchResult.getResultNote());

        return dto;
    }
}
