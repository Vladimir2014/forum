package com.ngtr.forum.dto;

import com.ngtr.forum.constraint.ValidPassword;

public class PasswordResetVerificationCodeRequest {
    private String username;
    @ValidPassword
    private String password;
    private int code;
    
	public String getUsername() { return username; }
	public int getCode() {	return code; }
	public String getPassword() { return password; }
	
	public void setUsername(String username) {	this.username = username; }
	public void setCode(int code) { this.code = code; }
	public void setPassword(String password) { this.password = password; }
}
