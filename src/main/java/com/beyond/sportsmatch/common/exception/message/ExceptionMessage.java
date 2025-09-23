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
    VOTE_INVALID_INPUT("투표 제목과 옵션은 필수입니다.", HttpStatus.BAD_REQUEST),
    // community
    POST_NOT_FOUND("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    POST_TITLE_BLANK("게시글은 제목을 입력해주세요.", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_CONTENT_BLANK("댓글의 내용이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND),
    NOT_POST_CREATOR("게시글 작성자가 아닙니다.",  HttpStatus.FORBIDDEN),
    NOT_COMMENT_CREATOR("댓글 작성자가 아닙니다.", HttpStatus.FORBIDDEN),
    ATTACHMENT_INVALID_NAME("올바른 첨부파일 이름이 아닙니다.", HttpStatus.BAD_REQUEST),
    ATTACHMENT_SAVE_FAILED("첨부파일 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ATTACHMENT_DELETE_FAILED("첨부파일 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_SEARCH_TYPE("현재 지원하지 않는 검색 방식입니다.", HttpStatus.BAD_REQUEST),
    SEARCH_KEYWORD_BLANK("검색어를 입력해 주세요.", HttpStatus.BAD_REQUEST),
    // friend
    FRIEND_LIST_NOT_FOUND("친구 목록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REQUEST_NOT_FOUND("해당 요청은 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ALREADY_SENT_REQUEST("이미 해당 유저에게 요청을 보냈습니다.", HttpStatus.CONFLICT),
    ALREADY_FRIEND("해당 유저와 이미 친구입니다.", HttpStatus.CONFLICT),
    ALREADY_DELETE_FRIEND("이미 삭제된 친구입니다.", HttpStatus.NOT_FOUND),
    ALREADY_RECEIVED_REQUEST("이미 요청을 받았습니다.", HttpStatus.CONFLICT),


    // match
    MATCH_APPLICATION_NOT_FOUND("매칭 신청 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MATCH_NOT_FOUND("매칭 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SPORT_NOT_FOUND("존재하지 않는 스포츠 종목입니다.", HttpStatus.NOT_FOUND),

    MATCH_RESULT_ALREADY_EXISTS("이미 결과가 등록된 경기입니다.", HttpStatus.CONFLICT),
    DUPLICATE_MATCH_APPLICATION("동일한 날짜에 이미 매칭을 신청했습니다.", HttpStatus.CONFLICT),
    CANNOT_CANCEL_MATCH_APPLICATION("WAITING 상태가 아니면 취소할 수 없음", HttpStatus.CONFLICT),

    INVALID_MATCH_DATE("매칭 날짜는 오늘 이후여야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_MATCH_TIME("종료 시간이 시작 시간보다 빠를 수 없습니다.", HttpStatus.BAD_REQUEST),


    // myPage
    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("인증이 필요합니다.", HttpStatus.UNAUTHORIZED);

    private final String message;

    private final HttpStatus status;
}
