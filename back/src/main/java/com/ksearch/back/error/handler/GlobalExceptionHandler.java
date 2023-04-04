package com.ksearch.back.error.handler;

import com.ksearch.back.error.exception.AuthException;
import com.ksearch.back.error.response.GlobalErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<GlobalErrorResponse> handleAuthExceptionHandler(AuthException exception) {
        return ResponseEntity
                .status(exception.getErrorCode().getStatusCode())
                .body(GlobalErrorResponse.from(exception.getErrorCode().getErrorMessage()));
    }
}