package com.beyond.sportsmatch.domain.match.model.dto;

import com.beyond.sportsmatch.domain.match.model.entity.MatchApplication;
import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDto {

    private String sport;

    private String region;

    private LocalDate matchDate;

    private String matchTime;

    private String genderOption;

    private long currentCount;

    private int requiredCount;

    public static MatchResponseDto fromEntity(MatchApplication application, Long waitingCount) {
        MatchResponseDto dto = new MatchResponseDto();

        dto.setSport(application.getSport().getName());
        dto.setRegion(application.getRegion());
        dto.setMatchDate(application.getMatchDate());
        String matchTime = application.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                " - " + application.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        dto.setMatchTime(matchTime);
        dto.setGenderOption(application.getGenderOption());
        dto.setCurrentCount(waitingCount);
        dto.setRequiredCount(application.getSport().getRequiredPersonnel());

        return dto;
    }
}
