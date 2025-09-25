package com.beyond.sportsmatch.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResponseDto {
    private Integer userId;
    private String userName;
    private String email;
}