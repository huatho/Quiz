package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
	@NotBlank
	private String oldPassword;
	@NotBlank
	private String newPassword;
	@NotBlank
	private String confirmPassword;
}