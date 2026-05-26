package com.example.demo.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.dto.QuestionRequest;
import com.example.demo.dto.QuestionResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.QuestionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/exams/{examId}/questions")
public class QuestionController {
	private final QuestionService questionService;

	public QuestionController(QuestionService questionService) {
		this.questionService = questionService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
			@Valid @RequestBody QuestionRequest questionRequest, 
			@PathVariable Long examId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		QuestionResponse questionResponse = questionService.createQuestion(questionRequest, examId,
				customUserDetails.getUser().getId());

		return ResponseEntity
				.status(SuccessCode.CREATED.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.SUCCESS, questionResponse));
	}

	@PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<List<QuestionResponse>>> importListQuestions(
			@RequestParam("file") MultipartFile file, 
			@PathVariable Long examId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		List<QuestionResponse> listQuestionResponse = questionService.importListQuestion(file, examId,
				customUserDetails.getUser().getId());

		return ResponseEntity
				.status(SuccessCode.CREATED.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.SUCCESS, listQuestionResponse));
	}

	@PutMapping("/{questionId}")
	public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
			@PathVariable Long examId,
			@PathVariable Long questionId, 
			@Valid @RequestBody QuestionRequest questionRequest,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		QuestionResponse questionResponse = questionService.updateQuestion(examId, questionId, questionRequest,
				customUserDetails.getUser().getId());

		return ResponseEntity
				.status(SuccessCode.UPDATED.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.UPDATED, questionResponse));
	}

	@DeleteMapping("/{questionId}")
	public ResponseEntity<ApiResponse<Void>> deleteQuestion(
			@PathVariable Long examId, 
			@PathVariable Long questionId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		questionService.deleteQuestion(examId, questionId, customUserDetails.getUser().getId());

		return ResponseEntity
				.status(SuccessCode.DELETED.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.DELETED, null));
	}
}
