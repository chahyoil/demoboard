package org.example.demoboard.exception;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;

@ControllerAdvice
public class CustomExceptionHandler {

    private final Environment env;

    public CustomExceptionHandler(Environment env) {
        this.env = env;
    }

    @ExceptionHandler(FetchException.class)
    public ResponseEntity<String> handleFetchException(FetchException ex, WebRequest request) {
        String msg = getMessageFromException(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("조회 오류: " + msg);
    }

    @ExceptionHandler(UpdateDeleteException.class)
    public ResponseEntity<String> handleUpdateDeleteException(UpdateDeleteException ex, WebRequest request) {
        String msg = getMessageFromException(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수정/삭제 오류: " + msg);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<String> handleRegistrationException(RegistrationException ex, WebRequest request) {
        String msg = getMessageFromException(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 오류: " + msg);
    }

    @ExceptionHandler(UnknownException.class)
    public ResponseEntity<String> handleUnknownException(UnknownException ex, WebRequest request) {
        String msg = getMessageFromException(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알수없음 오류: " + msg);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String msg = getMessageFromException(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알수없음 오류: " + msg);
    }

    private String getMessageFromException (Exception ex) {
        String msg = "";
        if(Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            msg = "";
        } else {
            msg = ex.getMessage();
        }
        return msg;
    }
}