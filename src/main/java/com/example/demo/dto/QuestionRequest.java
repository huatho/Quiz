package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionRequest {
	@NotBlank(message = "questionText không được để trống")
	private String questionText;

	@NotBlank(message = "answer1 không được để trống")
	private String answer1;

	@NotBlank(message = "answer2 không được để trống")
	private String answer2;

	@NotBlank(message = "answer3 không được để trống")
	private String answer3;

	@NotBlank(message = "answer4 không được để trống")
	private String answer4;

	@Min(1)
	@Max(4)
	private int correctAnswer;
}
