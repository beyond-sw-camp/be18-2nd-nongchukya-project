package com.beyond.sportsmatch.domain.match.model.entity;

public enum MatchStatus {
    WAITING,   // 매칭 대기중
    COMPLETED, // 매칭 완료
    CANCELED,  // 신청 취소
    EXPIRED    // 기간 만료
}
