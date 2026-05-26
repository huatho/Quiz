package com.example.demo.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	// Common
    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            "Internal server error",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    BAD_REQUEST(
            "INVALID_REQUEST",
            "Invalid request data",
            HttpStatus.BAD_REQUEST
    ),
    
    NOT_FOUND(
		"NOT_FOUND",
        "Resource not found",
        HttpStatus.NOT_FOUND
    ),
    
    API_NOT_FOUND(
    		"API_NOT_FOUND", 
    		"API endpoint not found", 
    		HttpStatus.NOT_FOUND
    ),

    // Auth
    BAD_CREDENTIALS(
            "BAD_CREDENTIALS",
            "Email or password is incorrect",
            HttpStatus.UNAUTHORIZED
    ),

    UNAUTHENTICATED(
            "UNAUTHENTICATED",
            "Authentication is required",
            HttpStatus.UNAUTHORIZED
    ),

    FORBIDDEN(
            "FORBIDDEN",
            "You do not have permission to access this resource",
            HttpStatus.FORBIDDEN
    ),
    
    EMAIL_ALREADY_EXISTS(
    		"EMAIL_ALREADY_EXISTS", 
    		"Email already exists", 
    		HttpStatus.CONFLICT
    ),

    // Access token
    ACCESS_TOKEN_EXPIRED(
            "ACCESS_TOKEN_EXPIRED",
            "Access token has expired",
            HttpStatus.UNAUTHORIZED
    ),

    INVALID_ACCESS_TOKEN(
            "INVALID_ACCESS_TOKEN",
            "Invalid access token",
            HttpStatus.UNAUTHORIZED
    ),

    MISSING_ACCESS_TOKEN(
            "MISSING_ACCESS_TOKEN",
            "Access token is missing",
            HttpStatus.UNAUTHORIZED
    ),

    // Refresh token
    INVALID_REFRESH_TOKEN(
            "INVALID_REFRESH_TOKEN",
            "Invalid refresh token",
            HttpStatus.UNAUTHORIZED
    ),

    REFRESH_TOKEN_EXPIRED(
            "REFRESH_TOKEN_EXPIRED",
            "Refresh token has expired",
            HttpStatus.UNAUTHORIZED
    ),

    TOKEN_USER_MISMATCH(
            "TOKEN_USER_MISMATCH",
            "Access token and refresh token do not belong to the same user",
            HttpStatus.UNAUTHORIZED
    );

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
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
