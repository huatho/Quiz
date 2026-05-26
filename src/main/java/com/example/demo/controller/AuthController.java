package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RefreshTokenResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RegisterResponse;
import com.example.demo.service.AuthService;
import com.example.demo.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController( AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
    		@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);

        return ResponseEntity
                .status(SuccessCode.SUCCESS.getHttpStatus())
                .body(ApiResponse.success(SuccessCode.SUCCESS, response));
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
    		@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.register(registerRequest);
        
        return ResponseEntity
        		.status(SuccessCode.SUCCESS.getHttpStatus())
        		.body(ApiResponse.success(SuccessCode.SUCCESS, response));
        
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
    		@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    	RefreshTokenResponse response = refreshTokenService.refresh(refreshTokenRequest);
    	
    	return ResponseEntity
        		.status(SuccessCode.SUCCESS.getHttpStatus())
        		.body(ApiResponse.success(SuccessCode.SUCCESS, response));
    }
}