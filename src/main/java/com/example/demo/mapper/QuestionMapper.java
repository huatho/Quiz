package com.example.demo.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.QuestionRequest;
import com.example.demo.dto.QuestionResponse;
import com.example.demo.entity.Question;

@Component
public class QuestionMapper {
	public Question requestToEntity(QuestionRequest questionRequest) {
		if (questionRequest == null) {
			return null;
		}
		Question question = new Question();
		question.setQuestionText(questionRequest.getQuestionText());
		question.setAnswer1(questionRequest.getAnswer1());
		question.setAnswer2(questionRequest.getAnswer2());
		question.setAnswer3(questionRequest.getAnswer3());
		question.setAnswer4(questionRequest.getAnswer4());
		question.setCorrectAnswer(questionRequest.getCorrectAnswer());

		return question;
	}

	public QuestionResponse entityToResponse(Question question) {

		return QuestionResponse.builder().id(question.getId()).questionText(question.getQuestionText())
				.answer1(question.getAnswer1()).answer2(question.getAnswer2()).answer3(question.getAnswer3())
				.answer4(question.getAnswer4()).correctAnswer(question.getCorrectAnswer()).build();
	}

	public void updateRequestToEntity(QuestionRequest questionRequest, Question question) {
		question.setQuestionText(questionRequest.getQuestionText());
		question.setAnswer1(questionRequest.getAnswer1());
		question.setAnswer2(questionRequest.getAnswer2());
		question.setAnswer3(questionRequest.getAnswer3());
		question.setAnswer4(questionRequest.getAnswer4());
		question.setCorrectAnswer(questionRequest.getCorrectAnswer());
	}
}
