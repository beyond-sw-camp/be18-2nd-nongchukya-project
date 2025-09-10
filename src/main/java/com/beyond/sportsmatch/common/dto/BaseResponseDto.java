package com.beyond.sportsmatch.common.dto;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
public class BaseResponseDto<T> {

    private final int code;

    private final String message;

    private final List<T> items;

    public BaseResponseDto(HttpStatus status, T item) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
        this.items = Collections.singletonList(item);
    }

    public BaseResponseDto(HttpStatus status, List<T> items) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
        this.items = items;
    }
}
