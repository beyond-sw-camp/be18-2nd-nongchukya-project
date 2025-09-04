package com.beyond.match.jwt.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResDto {
    private Integer userId;
    private String userName;
    private String email;
}