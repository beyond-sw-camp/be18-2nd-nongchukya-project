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
    MATCH_APPLICATION_NOT_FOUND("매칭 신청 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MATCH_NOT_FOUND("매칭 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SPORT_NOT_FOUND("존재하지 않는 스포츠 종목입니다.", HttpStatus.NOT_FOUND),

    MATCH_RESULT_ALREADY_EXISTS("이미 결과가 등록된 경기입니다.", HttpStatus.CONFLICT),
    DUPLICATE_MATCH_APPLICATION("동일한 날짜에 이미 매칭을 신청했습니다.", HttpStatus.CONFLICT),

    INVALID_MATCH_DATE("매칭 날짜는 오늘 이후여야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_MATCH_TIME("종료 시간이 시작 시간보다 빠를 수 없습니다.", HttpStatus.BAD_REQUEST);

    // myPage
    // user


    private final String message;

    private final HttpStatus status;
}
