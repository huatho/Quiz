package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.common.exception.EmailVerificationException;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RefreshTokenResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import com.example.demo.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    @Value("${baseurl.fe}")
    private String baseUrlFe;

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
    public ResponseEntity<ApiResponse<Void>> register(
    		@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        
        return ResponseEntity
        		.status(SuccessCode.CREATED.getHttpStatus())
        		.body(ApiResponse.success(SuccessCode.CREATED, null));
        
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
    		@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    	RefreshTokenResponse response = refreshTokenService.refresh(refreshTokenRequest);
    	
    	return ResponseEntity
        		.status(SuccessCode.SUCCESS.getHttpStatus())
        		.body(ApiResponse.success(SuccessCode.SUCCESS, response));
    }
    
    @GetMapping("/verify-email")
    public RedirectView  verifyEmail(@RequestParam("token") String token) {
    	try {
    		authService.verifyEmailToken(token);
    		return new RedirectView(baseUrlFe+ "/verify-email/success");
    	}
    	catch(EmailVerificationException e) {
    		return new RedirectView(baseUrlFe + "/verify-email/failed");
    	}
    }
}