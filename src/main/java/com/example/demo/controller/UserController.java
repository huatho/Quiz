package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UpdatePasswordRequest;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PutMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestBody UpdatePasswordRequest request,
			Authentication authentication) {
		String email = authentication.getName();
		userService.updatePassword(email, request);
		
		return ResponseEntity.ok("Password updated successfully");
	}
}