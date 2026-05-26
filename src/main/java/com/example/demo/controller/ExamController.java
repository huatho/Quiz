package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.dto.ExamRequest;
import com.example.demo.dto.ExamResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.ExamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/exams")
public class ExamController {

	private final ExamService examService;

	public ExamController(ExamService examService) {
		this.examService = examService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ExamResponse>>> getUserExams(
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		List<ExamResponse> listExamResponse = examService.getExamsByUser(customUserDetails.getUser());

		return ResponseEntity
				.status(SuccessCode.SUCCESS.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.SUCCESS, listExamResponse));
	}

	@GetMapping("/{examId}")
	public ResponseEntity<ApiResponse<ExamResponse>> getExam(
			@PathVariable Long examId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		ExamResponse examResponse = examService.getExam(examId, customUserDetails.getUser().getId());

		return ResponseEntity
				.status(SuccessCode.SUCCESS.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.SUCCESS, examResponse));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ExamResponse>> createExam(
			@Valid @RequestBody ExamRequest examRequest,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		ExamResponse examResponse = examService.createExam(examRequest, customUserDetails.getUser());

		return ResponseEntity
				.status(SuccessCode.CREATED.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.CREATED, examResponse));
	}

	@PutMapping("/{examId}")
	public ResponseEntity<ApiResponse<ExamResponse>> updateExam(
			@PathVariable Long examId,
			@Valid @RequestBody ExamRequest examRequest, 
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		ExamResponse examResponse = examService.updateExam(examId, examRequest, customUserDetails.getUser().getId());

		return ResponseEntity
				.status(SuccessCode.UPDATED.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.UPDATED, examResponse));
	}

	@DeleteMapping("/{examId}")
	public ResponseEntity<ApiResponse<Void>> deleteExam(
			@PathVariable Long examId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		examService.deleteExam(examId, customUserDetails.getUser().getId());

		return ResponseEntity
				.status(SuccessCode.DELETED.getHttpStatus())
				.body(ApiResponse.success(SuccessCode.DELETED, null));
	}
}
