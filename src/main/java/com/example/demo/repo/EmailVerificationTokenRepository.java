package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.EmailVerificationToken;
import com.example.demo.entity.User;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
	Optional<EmailVerificationToken> findByToken(String token);
	void deleteByUser(User user);

}
