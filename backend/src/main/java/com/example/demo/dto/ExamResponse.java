package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamResponse {
	private Long id;
	private String title;
	private double duration;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
