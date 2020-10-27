package com.ngtr.forum.controller;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ngtr.forum.dto.AuthenticationResponse;
import com.ngtr.forum.dto.LoginRequest;
import com.ngtr.forum.dto.PasswordResetVerificationCodeRequest;
import com.ngtr.forum.dto.RefreshTokenRequest;
import com.ngtr.forum.dto.RegisterRequest;
import com.ngtr.forum.service.AuthService;
import com.ngtr.forum.service.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Value("${domain}")
	private String domain;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) throws Exception {
		authService.signup(registerRequest);
		return new ResponseEntity<>("User Registration successful", HttpStatus.OK);
	}
	
	@GetMapping("/accountVerification/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable("token") String token) {
		authService.verifyAccount(token);
		return new ResponseEntity<>(getAccountActivatedHtml(), HttpStatus.OK);
	}
	
	private String getAccountActivatedHtml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<br/>");
		sb.append("Your account has been activated");
		sb.append("<br/><br/>");
		sb.append("You can now now <a href=\"" + domain + "login\">login</a> to your account");
		
		return sb.toString();
	}

	@PostMapping("/login")
	public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) throws Exception {
		return authService.login(loginRequest);
	}
	
	@PostMapping("/refresh/token")
	public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		return authService.refreshToken(refreshTokenRequest);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Refresh token deleted")
				;
	}
	
	@PostMapping("/code/send")
	public ResponseEntity<String> sendVerificationCodeForPasswordReset(@RequestBody String username) {
		
		try {
			authService.sendVerificationCodeForPasswordReset(username);
			return ResponseEntity
					.status(HttpStatus.OK)
					.body("Verification code sent")
					;
		} catch (MessagingException e) {
			e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("");
		}
	}
	
	@PostMapping("/code/verify")
	public ResponseEntity<String> verifyCodeForPasswordReset(@RequestBody PasswordResetVerificationCodeRequest passwordResetVerificationCodeRequest) {
		
		authService.verifyCodeForPasswordReset(passwordResetVerificationCodeRequest);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Verification code verified")
				;
	}
}
