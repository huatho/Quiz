package com.example.demo.common.response;

import org.springframework.http.HttpStatus;

import com.example.demo.common.exception.ErrorCode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
	private HttpStatus status;
	private String code;
	private String message;
	private T data;
	
	public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
	    return new ApiResponse<>(
	            successCode.getHttpStatus(),
	            successCode.getCode(),
	            successCode.getMessage(),
	            data
	    );
	}
	
	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
	    return new ApiResponse<>(
	    		errorCode.getHttpStatus(),
	    		errorCode.getCode(),
	    		errorCode.getMessage(),
	            null
	    );
	}
}