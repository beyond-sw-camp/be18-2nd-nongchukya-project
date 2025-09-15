package com.beyond.sportsmatch.domain.match.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id", unique = true)
    private MatchCompleted match;

    @Column(nullable = false)
    private String score;   // 예: "3:2"

    @Column
    private String winner;  // 예: "UserId=5" or "Team A"

    @Column
    private String resultNote; // 비고

    @Column
    private LocalDateTime createdAt;
}
