package com.example.demo.common.response;

import org.springframework.http.HttpStatus;

public enum SuccessCode {

    SUCCESS(
            "SUCCESS",
            "Success",
            HttpStatus.OK
    ),

    CREATED(
            "CREATED",
            "Created successfully",
            HttpStatus.CREATED
    ),

    UPDATED(
            "UPDATED",
            "Updated successfully",
            HttpStatus.OK
    ),

    DELETED(
            "DELETED",
            "Deleted successfully",
            HttpStatus.OK
    );

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    SuccessCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}