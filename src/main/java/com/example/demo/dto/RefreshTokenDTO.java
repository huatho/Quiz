package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenDTO {
	private String token;
	private LocalDateTime expiryDate;
}
