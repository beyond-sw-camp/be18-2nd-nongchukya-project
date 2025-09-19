package com.beyond.sportsmatch.common.exception.handler;

import com.beyond.sportsmatch.common.exception.ChatException;
import com.beyond.sportsmatch.common.exception.SportsMatchException;
import com.beyond.sportsmatch.common.exception.dto.ApiErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SportsMatchException.class)
    public ResponseEntity<Object> handleException(SportsMatchException e) {
        Map<String,Object> map = new HashMap<>();

        log.error("SportsMatchException: {}", e.getMessage());

        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(
                e.getStatus().value(),
                e.getType(),
                e.getMessage()
        );

        map.put("code", e.getStatus().value());
        map.put("status", e.getType());
        map.put("message", e.getMessage());

        return new ResponseEntity<>(apiErrorResponseDto, e.getStatus());
    }

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<Object> handleException(ChatException e) {
        Map<String,Object> map = new HashMap<>();
        log.error("ChatException: {}", e.getMessage());
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(
                e.getStatus().value(),
                e.getType(),
                e.getMessage()
        );
        map.put("code", e.getStatus().value());
        map.put("status", e.getType());
        map.put("message", e.getMessage());

        return new ResponseEntity<>(apiErrorResponseDto, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleException(MethodArgumentNotValidException e) {
        StringBuilder errors = new StringBuilder();

        log.error("MethodArgumentNotValidException: {}", e.getMessage());

        for(FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors
                    .append(fieldError.getField())
                    .append("(")
                    .append(fieldError.getDefaultMessage())
                    .append("), ");
        }

        errors.replace(errors.lastIndexOf(","),  errors.length(), "");

        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                errors.toString()
        );

        return new ResponseEntity<>(apiErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        Map<String,Object> map = new HashMap<>();

        log.error("Global Exception: {}", e.getMessage());

        map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.name());
        map.put("message", e.getMessage());

        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                e.getMessage()
        );

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
