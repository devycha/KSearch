package com.ksearch.back.error.exception;

import com.ksearch.back.error.type.AuthErrorCode;
import com.ksearch.back.error.type.EventErrorCode;

public class EventException extends RuntimeException {
    private final EventErrorCode errorCode;

    public EventException(EventErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
