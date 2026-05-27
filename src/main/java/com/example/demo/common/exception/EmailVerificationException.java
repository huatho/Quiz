package com.example.demo.common.exception;

@SuppressWarnings("serial")
public class EmailVerificationException extends RuntimeException {
	 private final ErrorCode errorCode;

    public EmailVerificationException(ErrorCode errorStatus) {
        super(errorStatus.getMessage());
        this.errorCode = errorStatus;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
