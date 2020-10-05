package com.ngtr.forum.dto;

import javax.validation.constraints.NotBlank;

public class RefreshTokenRequest {
	@NotBlank
	private String refreshToken;
	private Long userId;
	
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
