package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	Optional<Question> findByIdAndExamId(Long questionId, Long examId);

}
