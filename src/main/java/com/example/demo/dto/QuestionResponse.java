package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
	private Long id;
	private String questionText;
	private String answer1;
	private String answer2;
	private String answer3;
	private String answer4;
	private int correctAnswer;
}
