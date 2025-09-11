package com.beyond.sportsmatch.auth.model.security.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// 지금 예외 처리 하는 세부적인 사항들이 없다해야하나? 무튼 그쪽도 고민한번 해보쇼
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace(); // 콘솔에 상세 로그
        return ResponseEntity.badRequest().body("오류 발생: " + ex.getMessage());
    }
}
