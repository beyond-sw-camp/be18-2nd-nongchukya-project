package com.beyond.sportsmatch.domain.match.model.dto;

import com.beyond.sportsmatch.domain.match.model.entity.MatchCompleted;
import com.beyond.sportsmatch.domain.match.model.entity.MatchResult;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedMatchResponseDto {
    private int id;

    private String sport;

    private String region;

    private LocalDate matchDate;

    private String matchTime;

    private String genderOption;

    private int playerCount;

    private String[] participants;



    public static CompletedMatchResponseDto fromEntity(MatchCompleted match) {
        CompletedMatchResponseDto dto = new CompletedMatchResponseDto();
        // 엔티티의 필드 값을 DTO의 필드로 복사
        dto.setId(match.getMatchId());
        dto.setSport(match.getSport().getName());
        dto.setRegion(match.getRegion());
        dto.setMatchDate(match.getMatchDate());
        dto.setMatchTime(match.getMatchTime());
        dto.setGenderOption(match.getGenderOption());
        dto.setPlayerCount(match.getSport().getRequiredPersonnel());
        dto.setParticipants(match.getParticipants().stream().map(User::getNickname).toList().toArray(new String[0]));
        return dto;
    }
}
