package com.beyond.sportsmatch.common.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ApiErrorResponseDto {
    private final int code;

    private final String status;

    private final String message;

}
