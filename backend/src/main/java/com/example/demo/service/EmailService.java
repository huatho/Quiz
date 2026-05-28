package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.common.exception.AppException;
import com.example.demo.common.exception.ErrorCode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	private final JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
    private String fromEmail;
	
	public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
	
	public void sendVerifyEmail(String to, String subject, String verifyLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            String html = """
                    <h2>Verify your email</h2>
                    <p>Please click the link below to verify your account.</p>
                    <p>This link will be expired after 15 minutes.</p>
                    <a href="%s">Click here to verify</a>
                    """.formatted(verifyLink);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
        	System.out.println(e.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
