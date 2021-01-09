package com.ngtr.forum.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngtr.forum.constant.VerificationTokenTypeEnum;
import com.ngtr.forum.dto.AuthenticationResponse;
import com.ngtr.forum.dto.LoginRequest;
import com.ngtr.forum.dto.PasswordResetVerificationCodeRequest;
import com.ngtr.forum.dto.RefreshTokenRequest;
import com.ngtr.forum.dto.RegisterRequest;
import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.model.User;
import com.ngtr.forum.model.VerificationToken;
import com.ngtr.forum.repository.UserRepository;
import com.ngtr.forum.repository.VerificationTokenRepository;
import com.ngtr.forum.security.JwtProvider;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private RefreshTokenService refreshTokenService;

	@Transactional
	public void signup(RegisterRequest registerRequest)
	  throws Exception 
	{
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setEnabled(false);
		user.setCreatedOn(Instant.now());
		
		userRepository.save(user);
		
		mailService.sendActivationEmail(user.getEmail(), 
										generateAccountVerificationToken(user));
	}

	@Transactional
	private String generateAccountVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationToken.setVerificationTokenTypeEnumId(VerificationTokenTypeEnum.REGISTRATION.getId());
		verificationToken.setExpirationDate(Instant.now().plusSeconds(60*60));
		
		verificationTokenRepository.save(verificationToken);
		
		return token;
	}

	@Transactional
	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
		verificationToken.orElseThrow(() -> new ForumException("Invalid token"));
		fetchUserAndEnable(verificationToken.get());
	}

	public AuthenticationResponse login(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtProvider.generateToken(authentication);
		AuthenticationResponse authenticationResponse = new AuthenticationResponse();
		authenticationResponse.setAuthenticationToken(token);
		authenticationResponse.setUsername(loginRequest.getUsername());
		authenticationResponse.setExpiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()));
		authenticationResponse.setRefreshToken(refreshTokenService.generateRefreshToken().getToken());
		
		return authenticationResponse;
	}

	@Transactional (readOnly = true)
	public User getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new ForumException("User not found"));
	}

	public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.validateToken(refreshTokenRequest.getRefreshToken());
		
		String token = jwtProvider.generateToken(refreshTokenRequest.getUsername());
				
		AuthenticationResponse authenticationResponse = new AuthenticationResponse();
		authenticationResponse.setAuthenticationToken(token);
		authenticationResponse.setExpiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()));
		authenticationResponse.setRefreshToken(refreshTokenRequest.getRefreshToken());
		authenticationResponse.setUsername(refreshTokenRequest.getUsername());
		
		return authenticationResponse;
	}

	public boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}

	public void sendVerificationCodeForPasswordReset(String username) throws MessagingException {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new ForumException("User not found"));
		
		mailService.sendPasswordResetConfirmationCodeEmail(user.getUsername(),
														   user.getEmail(), 
														   generateVerificationTokenForPasswordReset(user));
	}
	
	@Transactional(readOnly=true)
	public void verifyCodeForPasswordReset(PasswordResetVerificationCodeRequest passwordResetVerificationCodeRequest) {
		VerificationToken verificationToken = getVerifyCodeForPasswordReset(passwordResetVerificationCodeRequest);
		String username = passwordResetVerificationCodeRequest.getUsername();
		
		if (!verificationToken.getUser().getUsername().equals(username)) {
			throw new ForumException("Invalid token");
		}
	}
	
	@Transactional
	public void resetPassword(PasswordResetVerificationCodeRequest passwordResetVerificationCodeRequest) {
		VerificationToken verificationToken = getVerifyCodeForPasswordReset(passwordResetVerificationCodeRequest);
		String username = passwordResetVerificationCodeRequest.getUsername();
		
		if (!verificationToken.getUser().getUsername().equals(username)) {
			throw new ForumException("Invalid token");
		}
		
		verificationToken.getUser().setPassword(passwordEncoder.encode(passwordResetVerificationCodeRequest.getPassword()));
		verificationTokenRepository.deleteAllTokens(verificationToken.getUser(), VerificationTokenTypeEnum.PASSWORD_RESET.getId());
	}
	
	@Transactional(readOnly=true)
	private VerificationToken getVerifyCodeForPasswordReset(PasswordResetVerificationCodeRequest passwordResetVerificationCodeRequest) {
		return (verificationTokenRepository
			    .findByTokenAndVerificationTokenTypeEnumId(String.valueOf(passwordResetVerificationCodeRequest.getCode()), 
														   VerificationTokenTypeEnum.PASSWORD_RESET.getId())
			    .orElseThrow(() -> new ForumException("Invalid token")));
		
	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		if (verificationToken.isExpired()) throw new ForumException("Token Expired");
		Long userId = verificationToken.getUser().getUserId();
		User user = userRepository.findById(userId).orElseThrow(() -> new ForumException("User not found"));
		user.setEnabled(true);
		userRepository.save(user);
	}

	@Transactional
	private String generateVerificationTokenForPasswordReset(User user) {
		String token = String.valueOf(generateVerificationCodeForPasswordReset());
		
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationToken.setVerificationTokenTypeEnumId(VerificationTokenTypeEnum.PASSWORD_RESET.getId());
		verificationToken.setExpirationDate(Instant.now().plusSeconds(600));
		
		verificationTokenRepository.save(verificationToken);
		
		return token;
	}
	
	private int generateVerificationCodeForPasswordReset() {
		int min = 10000;
		int max = 99999;
		
		return (int)(Math.random() * (max - min + 1) + min);
	}

}
