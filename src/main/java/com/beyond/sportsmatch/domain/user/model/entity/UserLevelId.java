package com.beyond.sportsmatch.domain.user.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UserLevelId implements Serializable {

    private Integer userId;

    private Integer sportId;

    public UserLevelId() {}

    public UserLevelId(Integer userId, Integer sportId) {
        this.userId = userId;
        this.sportId = sportId;
    }
}