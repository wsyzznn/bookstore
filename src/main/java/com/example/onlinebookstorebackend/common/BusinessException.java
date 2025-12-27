package com.example.onlinebookstorebackend.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认业务错误
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}