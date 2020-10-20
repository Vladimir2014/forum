package com.ngtr.forum.dto;

import java.time.Instant;

public class AuthenticationResponse {
	private Long id;
	private String username;
	private String authenticationToken;
	private String refreshToken;
	private Instant expiresAt;
	
	public void setId(Long id) { this.id = id; }
	public void setUsername(String username) { this.username = username; }
	public void setAuthenticationToken(String authenticationToken) { this.authenticationToken = authenticationToken; }
	public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
	public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

	public Long getId() { return id; }
	public String getUsername() { return username; }
	public String getAuthenticationToken() { return authenticationToken; }
	public String getRefreshToken() { return refreshToken; }
	public Instant getExpiresAt() { return expiresAt; }
}
