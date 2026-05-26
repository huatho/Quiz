package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.exception.AppException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.dto.QuestionRequest;
import com.example.demo.dto.QuestionResponse;
import com.example.demo.entity.Exam;
import com.example.demo.entity.Question;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.repo.ExamRepository;
import com.example.demo.repo.QuestionRepository;

@Service
public class QuestionService {
	private final ExamRepository examRepository;
	private final QuestionRepository questionRepository;
	private final QuestionMapper questionMapper;

	public QuestionService(ExamRepository examRepository, QuestionRepository questionRepository,
			QuestionMapper questionMapper) {
		this.examRepository = examRepository;
		this.questionRepository = questionRepository;
		this.questionMapper = questionMapper;
	}

	private Exam getOwnedExam(Long examId, Long userId) {
		Exam exam = examRepository.findByIdAndUserId(examId, userId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

		return exam;
	}
	
	private Question getQuestionInExam(Long questionId, Long examId) {
		Question question = questionRepository.findByIdAndExamId(questionId, examId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
		
		return question;
	}

	public QuestionResponse createQuestion(QuestionRequest questionRequest, Long examId, Long userId) {
		Exam exam = getOwnedExam(examId, userId);
		Question question = questionMapper.requestToEntity(questionRequest);
		question.setExam(exam);
		Question savedQuestion = questionRepository.save(question);
		exam.setUpdatedAt(LocalDateTime.now());
		examRepository.save(exam);

		return questionMapper.entityToResponse(savedQuestion);
	}

	public List<QuestionResponse> importListQuestion(MultipartFile file, Long examId, Long userId) {
		if (file == null || file.isEmpty()) {
			throw new AppException(ErrorCode.BAD_REQUEST);
		}
		Exam exam = getOwnedExam(examId, userId);
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			List<Question> questions = new ArrayList<Question>();

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String questionText = formatter.formatCellValue(row.getCell(0));
				String answer1 = formatter.formatCellValue(row.getCell(1));
				String answer2 = formatter.formatCellValue(row.getCell(2));
				String answer3 = formatter.formatCellValue(row.getCell(3));
				String answer4 = formatter.formatCellValue(row.getCell(4));
				String correctAnswerText = formatter.formatCellValue(row.getCell(5)).trim();
				if (questionText.isBlank() || answer1.isBlank() || answer2.isBlank() || answer3.isBlank()
						|| answer4.isBlank() || correctAnswerText.isBlank()) {
					throw new AppException(ErrorCode.BAD_REQUEST);
				}
				int correctAnswer = Integer.parseInt(correctAnswerText);
				if (correctAnswer < 1 || correctAnswer > 4) {
					throw new AppException(ErrorCode.BAD_REQUEST);
				}

				Question question = new Question();
				question.setQuestionText(questionText);
				question.setAnswer1(answer1);
				question.setAnswer2(answer2);
				question.setAnswer3(answer3);
				question.setAnswer4(answer4);
				question.setCorrectAnswer(correctAnswer);
				question.setExam(exam);

				questions.add(question);
			}
			List<Question> listSavedQuestion = questionRepository.saveAll(questions);
			exam.setUpdatedAt(LocalDateTime.now());
			examRepository.save(exam);
			return listSavedQuestion.stream().map(q -> {
				return questionMapper.entityToResponse(q);
			}).toList();

		} catch (Exception e) {
			throw new AppException(ErrorCode.BAD_REQUEST);
		}
	}

	public QuestionResponse updateQuestion(Long examId, Long questionId, QuestionRequest questionRequest, Long userId) {
		Exam exam = getOwnedExam(examId, userId);
		Question question = getQuestionInExam(questionId, examId);
		questionMapper.updateRequestToEntity(questionRequest, question);
		Question updatedQuestion = questionRepository.save(question);
		exam.setUpdatedAt(LocalDateTime.now());
		examRepository.save(exam);
		return questionMapper.entityToResponse(updatedQuestion);
	}

	public void deleteQuestion(Long examId, Long questionId, Long userId) {
		Exam exam = getOwnedExam(examId, userId);
		Question question = getQuestionInExam(questionId, exam.getId());
		questionRepository.delete(question);
	}
}
