package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.exception.AppException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.common.utils.Utils;
import com.example.demo.dto.RefreshTokenDTO;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RefreshTokenResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repo.RefreshTokenRepository;
import com.example.demo.security.JwtService;

@Service
public class RefreshTokenService {
	@Value("${refresh-token-expiration-days}")
	private int expirationDay;
	private final RefreshTokenRepository refreshTokenRepo;
	private final JwtService jwtService;
		
	public RefreshTokenService(RefreshTokenRepository refreshTokenRepo, JwtService jwtService) {
		this.refreshTokenRepo = refreshTokenRepo;
		this.jwtService = jwtService;
	}
	
	@Transactional
	public RefreshTokenDTO createToken(User user) {
		refreshTokenRepo.deleteByUserId(user.getId());
		RefreshToken refreshToken = new RefreshToken();
		String rawToken = Utils.generateSecureRandomToken();
		refreshToken.setToken(hashToken(rawToken));
		refreshToken.setUser(user);
		refreshToken.setExpiryDate(LocalDateTime.now().plusDays(expirationDay));
		RefreshToken savedRefreshToken = refreshTokenRepo.save(refreshToken);
		
		return RefreshTokenDTO.builder().token(rawToken).expiryDate(savedRefreshToken.getExpiryDate()).build();
	}
	
	@Transactional
	public RefreshTokenResponse refresh(RefreshTokenRequest refreshTokenRequest) {
		String hashedToken = hashToken(refreshTokenRequest.getRefreshToken());
		RefreshToken oldRefreshToken = refreshTokenRepo.findByToken(hashedToken)
				.orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));
		if (oldRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			refreshTokenRepo.delete(oldRefreshToken);
			throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}
		User user = oldRefreshToken.getUser();
		String accessToken = jwtService.generateToken(user.getEmail());
		RefreshTokenDTO refreshToken = createToken(user);
		
		return RefreshTokenResponse
				.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken.getToken())
				.refreshTokenExpiryDate(refreshToken.getExpiryDate())
				.build();
	}
	
	private String hashToken(String token) {
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
	        
	        return HexFormat.of().formatHex(hash);
	    } catch (Exception e) {
	        throw new RuntimeException("Error hashing token", e);
	    }
	}
}
