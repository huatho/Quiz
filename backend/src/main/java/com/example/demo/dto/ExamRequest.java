package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ExamRequest {
	@NotBlank
	private String title;

	@Positive
	private double duration;

}
