package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.common.exception.AppException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.dto.ExamRequest;
import com.example.demo.dto.ExamResponse;
import com.example.demo.entity.Exam;
import com.example.demo.entity.User;
import com.example.demo.mapper.ExamMapper;
import com.example.demo.repo.ExamRepository;

@Service
public class ExamService {
	private final ExamRepository examRepository;
	private final ExamMapper examMapper;

	public ExamService(ExamRepository examRepository, ExamMapper examMapper) {
		this.examRepository = examRepository;
		this.examMapper = examMapper;
	}

	private Exam getOwnedExam(Long examId, Long userId) {
		Exam exam = examRepository.findByIdAndUserId(examId, userId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

		return exam;
	}

	public List<ExamResponse> getExamsByUser(User user) {
		List<Exam> listExam = examRepository.findAllByUserId(user.getId());

		return listExam.stream().map(exam -> examMapper.entityToResponse(exam)).toList();
	}

	public ExamResponse getExam(Long examId, Long userId) {
		Exam exam = getOwnedExam(examId, userId);

		return examMapper.entityToResponse(exam);
	}

	public ExamResponse createExam(ExamRequest examRequest, User currentUser) {
		Exam exam = examMapper.requestToEntity(examRequest);
		exam.setUser(currentUser);
		exam.setCreatedAt(LocalDateTime.now());
		exam.setUpdatedAt(null);

		Exam savedExam = examRepository.save(exam);
		ExamResponse examResponse = ExamResponse.builder().id(savedExam.getId()).title(savedExam.getTitle())
				.duration(savedExam.getDuration()).build();
		return examResponse;
	}

	public ExamResponse updateExam(Long examId, ExamRequest examRequest, Long userId) {
		Exam exam = getOwnedExam(examId, userId);
		examMapper.updateRequestToEntity(examRequest, exam);
		Exam updatedExam = examRepository.save(exam);

		return examMapper.entityToResponse(updatedExam);
	}

	public void deleteExam(Long examId, Long userId) {
		Exam exam = getOwnedExam(examId, userId);
		examRepository.delete(exam);
	}
}
