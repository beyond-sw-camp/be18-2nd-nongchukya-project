package com.beyond.sportsmatch.domain.user.model.entity;



import com.beyond.sportsmatch.common.entity.BaseTimeEntity;
import com.beyond.sportsmatch.domain.match.model.entity.Sport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_level")
public class UserLevel extends BaseTimeEntity {

    @EmbeddedId
    private UserLevelId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sportId")
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    private Boolean interest = false;
}