package com.example.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.common.exception.AppException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenDTO;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RegisterResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
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

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, 
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.STUDENT);

        userRepository.save(user);

        return new RegisterResponse("Register successfully", user.getEmail());
    }
    
}
