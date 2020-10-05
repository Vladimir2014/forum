package com.ngtr.forum.security;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.repository.UserRepository;

import io.jsonwebtoken.Jwts;

@Service
public class JwtProvider {

	@Value("secret")
	private String keySecret;
	
	@Value("${jwt.expiration.time}")
	private Long jwtExpirationInMillis;
	
	private KeyStore keyStore;
	
	@PostConstruct
	public void init() {
		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream resourceAsStream = getClass().getResourceAsStream("/forum.jks");
			keyStore.load(resourceAsStream, keySecret.toCharArray());
			
		} catch (Exception e) {
			throw new ForumException("Keystore exception: " + e.getMessage());
		}
		
	}
	
	public String generateToken(Authentication authentication) {
		User principal = (User)authentication.getPrincipal();
		
		return Jwts.builder()
				.setSubject(principal.getUsername()) //TODO change to userId
				.signWith(getPrivateKey())
				.setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
				.compact()
				;
	}
	
	public String generateToken(String userName) {
		return Jwts.builder()
				.setSubject(userName)
				.signWith(getPrivateKey())
				.setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
				.compact()
				;
	}
	
	public boolean validateToken(String jwtToken) {
		
		System.out.println("PUBLIC TOKEN: " + getPublicKey());
		Jwts.parser()
		.setSigningKey(getPublicKey())
		.parseClaimsJws(jwtToken)
		;
		
		return true;
	}
	
	public String getUserNameFromToken(String jwtToken) {
		return Jwts
				.parser()
				.setSigningKey(getPublicKey())
				.parseClaimsJws(jwtToken)
				.getBody()
				.getSubject();
	}
	
	public Long getJwtExpirationInMillis() {
		return jwtExpirationInMillis;
	}
	
	private PublicKey getPublicKey() {
		try {
			return keyStore.getCertificate("forum").getPublicKey();
		} catch (KeyStoreException ke) {
			throw new ForumException("Certificate Excpetion: " + ke.getMessage());
		}
	}

	private PrivateKey getPrivateKey() {
		try {
			return (PrivateKey)keyStore.getKey("forum", keySecret.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new ForumException("Exception occured while retreiving public key form keystore");
		}
	}

}
