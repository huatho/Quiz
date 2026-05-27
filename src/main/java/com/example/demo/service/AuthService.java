package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.exception.AppException;
import com.example.demo.common.exception.EmailVerificationException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.common.utils.Utils;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenDTO;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.EmailVerificationToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repo.EmailVerificationTokenRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtService;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    @Value("${baseurl.be}")
    private String baseUrl;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, 
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       EmailVerificationTokenRepository emailVerificationTokenRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
    }
    
    public LoginResponse login(LoginRequest loginRequest) {
    	Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                		loginRequest.getEmail(),
                		loginRequest.getPassword()
                )
        );
    	CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
        String email = authentication.getName();
        String accessToken = jwtService.generateToken(email);
        RefreshTokenDTO refreshTokenDTO = refreshTokenService.createToken(userDetail.getUser());
        LoginResponse loginResponse = LoginResponse.builder()
        		.accessToken(accessToken)
        		.refreshToken(refreshTokenDTO.getToken())
        		.refreshTokenExpiryDate(refreshTokenDTO.getExpiryDate()).build();
        
        return loginResponse;
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
    	User user = userRepository.findByEmail(registerRequest.getEmail()).orElseGet(User::new);
    	if (user.isActive()) {
    		throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
    	}
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.STUDENT);
        User savedUser = userRepository.save(user);
        // Create email verification token
        emailVerificationTokenRepository.deleteByUser(savedUser);
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        String token = Utils.generateSecureRandomToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setExpiredAt(LocalDateTime.now().plusMinutes(15));
        emailVerificationToken.setUser(savedUser);
        emailVerificationTokenRepository.save(emailVerificationToken);
        // Send email
        String verifyLink = baseUrl + "/auth/verify-email" + "?token=" + token;
        emailService.sendVerifyEmail(savedUser.getEmail(), "QUIZ SYSTEM EMAIL", verifyLink);
    }
    
    @Transactional
    public void verifyEmailToken(String token) {
    	EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
    			.orElseThrow(() -> new EmailVerificationException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN));
    	if (emailVerificationToken.getExpiredAt().isAfter(LocalDateTime.now())) {
    		User user = emailVerificationToken.getUser();
    		user.setActive(true);
    		userRepository.save(user);
    		emailVerificationTokenRepository.delete(emailVerificationToken);
    	}
    	else {
    		emailVerificationTokenRepository.delete(emailVerificationToken);
    		throw new EmailVerificationException(ErrorCode.EMAIL_VERIFICATION_TOKEN_EXPIRED);
    	}
    }
    
}
