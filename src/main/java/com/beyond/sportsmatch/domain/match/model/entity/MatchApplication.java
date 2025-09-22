package com.beyond.sportsmatch.domain.match.model.entity;

import com.beyond.sportsmatch.domain.match.model.dto.MatchApplicationRequestDto;
import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class MatchApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int matchApplicationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Column
    private String region;

    @Column
    private LocalDate matchDate;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column
    private String genderOption;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant_id")
    private User applicantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    // 처음 생성 시 status를 WAITING으로 자동 설정
    @PrePersist
    public void prePersist() {
        this.status = this.status == null ? MatchStatus.WAITING : this.status;
    }

    public void setMatchApplication(MatchApplicationRequestDto matchApplicationRequestDto, User user, Sport sport) {
        this.sport = sport;
        this.region = matchApplicationRequestDto.getRegion();
        this.matchDate = matchApplicationRequestDto.getMatchDate();
        this.startTime = matchApplicationRequestDto.getStartTime();
        this.endTime = matchApplicationRequestDto.getEndTime();
        this.genderOption = matchApplicationRequestDto.getGenderOption();
        this.createdAt = LocalDateTime.now();
        this.applicantId = user;
    }
}
