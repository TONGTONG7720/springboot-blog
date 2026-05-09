package com.example.blog.exception;

import lombok.Getter;

/**
 * 业务异常
 * 知识点: 自定义异常类，用于业务层抛出有意义的错误
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
