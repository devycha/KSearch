package com.ksearch.back.error.exception;

import com.ksearch.back.error.type.AuthErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException{
    private final AuthErrorCode errorCode;

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
