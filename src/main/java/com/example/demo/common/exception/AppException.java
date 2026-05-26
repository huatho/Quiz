package com.example.demo.common.exception;

@SuppressWarnings("serial")
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorStatus) {
        super(errorStatus.getMessage());
        this.errorCode = errorStatus;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}