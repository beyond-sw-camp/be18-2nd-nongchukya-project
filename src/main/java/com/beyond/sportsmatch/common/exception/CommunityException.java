package com.beyond.sportsmatch.common.exception;

import com.beyond.sportsmatch.common.exception.message.ExceptionMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class CommunityException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String type;

    private final HttpStatus status;

  public CommunityException(ExceptionMessage exceptionMessage) {
      super(exceptionMessage.getMessage());

      this.type = exceptionMessage.name();
      this.status = exceptionMessage.getStatus();
    }
}
