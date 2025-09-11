package com.beyond.sportsmatch.domain.match.model.dto;

import com.beyond.sportsmatch.domain.match.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDto {
    private int matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Sport sport;

    @Column
    private String region;

    @Column
    private LocalDate matchDate;

    @Column
    private String matchTime;

    @Column
    private String genderOption;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private User applicantId;
}
