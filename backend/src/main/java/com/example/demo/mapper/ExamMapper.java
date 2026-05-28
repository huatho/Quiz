package com.example.demo.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.demo.dto.ExamRequest;
import com.example.demo.dto.ExamResponse;
import com.example.demo.entity.Exam;

@Component
public class ExamMapper {
	public ExamResponse entityToResponse(Exam exam) {
		return ExamResponse.builder().id(exam.getId()).title(exam.getTitle()).duration(exam.getDuration())
				.createdAt(exam.getCreatedAt()).updatedAt(exam.getUpdatedAt()).build();
	}

	public Exam requestToEntity(ExamRequest examRequest) {
		Exam exam = new Exam();
		exam.setTitle(examRequest.getTitle());
		exam.setDuration(examRequest.getDuration());

		return exam;
	}

	public void updateRequestToEntity(ExamRequest examRequest, Exam exam) {
		exam.setTitle(examRequest.getTitle());
		exam.setDuration(examRequest.getDuration());
		exam.setUpdatedAt(LocalDateTime.now());
	}
}
