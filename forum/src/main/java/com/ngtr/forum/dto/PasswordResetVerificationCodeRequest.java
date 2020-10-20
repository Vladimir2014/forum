package com.ngtr.forum.dto;

public class PasswordResetVerificationCodeRequest {
    private String username;
    private int code;
    
	public String getUsername() {
		return username;
	}
	public int getCode() {
		return code;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
