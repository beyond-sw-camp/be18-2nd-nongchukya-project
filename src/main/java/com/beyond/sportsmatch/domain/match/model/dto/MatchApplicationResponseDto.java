package com.beyond.sportsmatch.domain.match.model.dto;

import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchApplicationResponseDto {
    private String sport;

    private String region;

    private LocalDate matchDate;

    private String matchTime;

    private String genderOption;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public MatchApplicationResponseDto(MatchApplication matchApplication) {
        this.sport = matchApplication.getSport().getName();
        this.region = matchApplication.getRegion();
        this.matchDate = matchApplication.getMatchDate();
        this.matchTime = matchApplication.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " - " + matchApplication.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.genderOption = matchApplication.getGenderOption();
        this.createdAt = matchApplication.getCreatedAt();
    }
}
