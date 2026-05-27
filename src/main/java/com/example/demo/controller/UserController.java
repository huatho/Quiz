package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.dto.UpdatePasswordRequest;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PutMapping("/me/password")
	public ResponseEntity<ApiResponse<Void>> changePassword(
			@Valid @RequestBody UpdatePasswordRequest request,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		userService.updatePassword(customUserDetails.getEmail(), request);
		
		return ResponseEntity
				.status(SuccessCode.SUCCESS.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.SUCCESS, null));
	}
}