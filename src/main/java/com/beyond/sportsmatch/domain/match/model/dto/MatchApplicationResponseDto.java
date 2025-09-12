package com.beyond.sportsmatch.domain.match.model.dto;

import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchApplicationResponseDto {
    private int matchApplicationId;

    private String sport;

    private String region;

    private LocalDate matchDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String genderOption;

    private LocalDateTime createdAt;

    public MatchApplicationResponseDto(MatchApplication matchApplication) {
        this.matchApplicationId = matchApplication.getMatchApplicationId();
        this.sport = matchApplication.getSport().getName();
        this.region = matchApplication.getRegion();
        this.matchDate = matchApplication.getMatchDate();
        this.startTime = matchApplication.getStartTime();
        this.endTime = matchApplication.getEndTime();
        this.genderOption = matchApplication.getGenderOption();
        this.createdAt = matchApplication.getCreatedAt();
    }
}
