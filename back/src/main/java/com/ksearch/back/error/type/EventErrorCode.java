package com.ksearch.back.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EventErrorCode {
    OutOfStock(HttpStatus.BAD_REQUEST, "이벤트 수량 재고가 없습니다."),
    EventNotFound(HttpStatus.NOT_FOUND,"없는 이벤트 정보입니다.");

    private final HttpStatus statusCode;
    private final String errorMessage;
}
