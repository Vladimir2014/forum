package com.ngtr.forum.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.ngtr.forum.model.NotificationEmail;
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
	throws Exception {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setEnabled(false);
		user.setCreatedOn(Instant.now());
		
		userRepository.save(user);
		
		String token = generateVerificationToken(user);
		
		mailService.sendMail(new NotificationEmail("Please Activate your account",
				user.getEmail(),
				"http://localhost:8081/api/auth/accountVerification/" + token));
	}

	@Transactional
	private String generateVerificationToken(User user) {
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

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		if (verificationToken.isExpired()) throw new ForumException("Token Expired");
		Long userId = verificationToken.getUser().getUserId();
		User user = userRepository.findById(userId).orElseThrow(() -> new ForumException("User not found"));
		user.setEnabled(true);
		userRepository.save(user);
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
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ForumException("User not found"));
	}

	public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.validateToken(refreshTokenRequest.getRefreshToken());
		
		User authenticatedUser = getCurrentUser();
		String token = jwtProvider.generateToken(authenticatedUser.getUsername());
				
		AuthenticationResponse authenticationResponse = new AuthenticationResponse();
		authenticationResponse.setAuthenticationToken(token);
		authenticationResponse.setExpiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()));
		authenticationResponse.setRefreshToken(refreshTokenRequest.getRefreshToken());
		authenticationResponse.setUsername(authenticatedUser.getUsername());
		
		return authenticationResponse;
	}

	public boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}

	public boolean sendVerificationCode(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		
		if (user.isPresent()) {
			String code = generateVerificationTokenForPasswordReset(user.get());
			
			mailService.sendMail(new NotificationEmail("Password reset verification code",
														user.get().getEmail(),
														"Verification code: " + code));					
			
			return true;
		}
		
		throw new ForumException("Unable to find user: " + username);
	}
	
	@Transactional
	private String generateVerificationTokenForPasswordReset(User user) {
		String token = UUID.randomUUID().toString();
		
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(generatePasswordResetVerificationCode() + "");
		verificationToken.setUser(user);
		verificationToken.setVerificationTokenTypeEnumId(VerificationTokenTypeEnum.PASSWORD_RESET.getId());
		verificationToken.setExpirationDate(Instant.now().plusSeconds(600));
		
		verificationTokenRepository.save(verificationToken);
		
		return token;
	}

	private int generatePasswordResetVerificationCode() {
		int min = 10000;
		int max = 99999;
	
		return (int)(Math.random() * (max - min + 1) + min);
	}

	@Transactional
	public boolean verifyPasswordResetCode(PasswordResetVerificationCodeRequest passwordResetVerificationCodeRequest) {
		String username = passwordResetVerificationCodeRequest.getUsername();
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByTokenAndVerificationTokenTypeEnumId(passwordResetVerificationCodeRequest.getCode()+"", 																																VerificationTokenTypeEnum.PASSWORD_RESET.getId());
		verificationToken.orElseThrow(() -> new ForumException("Invalid token"));
		
		if (verificationToken.get().getUser().getUsername().equals(username)) {
			return true;
		}
		
		throw new ForumException("Invalid token");
	}
	



}
