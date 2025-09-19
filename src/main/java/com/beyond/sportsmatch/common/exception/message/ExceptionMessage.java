package com.beyond.sportsmatch.common.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    // chat
    CHATROOM_NOT_FOUND("채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHATROOM_NOT_GROUP("그룹 채팅방이 아닙니다.", HttpStatus.BAD_REQUEST),
    NOT_CHAT_MEMBER("해당 채팅방의 참여자가 아닙니다.", HttpStatus.FORBIDDEN),
    COMPLETED_MATCH_NOT_FOUND("성사된 매칭이 없습니다.",  HttpStatus.NOT_FOUND),
    VOTE_NOT_FOUND("투표를 찾을 수 없습니다.",  HttpStatus.NOT_FOUND),
    ALREADY_CASTED_VOTE("이미 투표를 완료하였습니다.", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND("알림을  찾을 수 없습니다.",  HttpStatus.NOT_FOUND),
    FORBIDDEN_NOTIFICATION_DELETE("내 알림만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    LOGINID_NOT_FOUND("로그인 아이디가 없습니다.",   HttpStatus.NOT_FOUND),
    // community
    // friend
    // match
    MATCH_APPLICATION_NOT_FOUND("매칭 신청 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),







    // myPage
    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("인증이 필요합니다.", HttpStatus.UNAUTHORIZED);

    private final String message;

    private final HttpStatus status;
}
