package com.beyond.sportsmatch.domain.match.model.entity;

import com.beyond.sportsmatch.domain.user.model.entity.Sport;
import com.beyond.sportsmatch.domain.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class MatchCompleted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private int matchId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sport_id")
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "match_participants", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "match_id"), // MatchCompleted의 ID
            inverseJoinColumns = @JoinColumn(name = "user_id") // User의 ID
    )
    private Set<User> participants = new HashSet<>();
}
