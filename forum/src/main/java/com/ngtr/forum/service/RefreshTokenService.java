package com.ngtr.forum.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.model.RefreshToken;
import com.ngtr.forum.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public RefreshToken generateRefreshToken() {
		RefreshToken refreshToken = new RefreshToken();
		
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setCreatedDate(Instant.now());
		
		return refreshTokenRepository.save(refreshToken);
	}
	
	@Transactional(readOnly = true)
	public void validateToken(String token) {
		refreshTokenRepository
		.findByToken(token)
		.orElseThrow(() -> new ForumException("invalid refresh token"));
	}
	
	@Transactional
	public void deleteRefreshToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}
}
