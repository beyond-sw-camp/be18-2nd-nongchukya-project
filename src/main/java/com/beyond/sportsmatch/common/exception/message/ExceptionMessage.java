package com.beyond.sportsmatch.common.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    // chat
    // community
    // friend
    // match
    MATCH_APPLICATION_NOT_FOUND("매칭 신청 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);







    // myPage
    // user


    private final String message;

    private final HttpStatus status;
}
