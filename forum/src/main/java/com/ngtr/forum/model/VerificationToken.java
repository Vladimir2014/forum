package com.ngtr.forum.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="token")
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String token;
	
	@OneToOne(fetch = FetchType.LAZY)
	private User user;
	
	private Instant expirationDate;
	
	private int verificationTokenTypeEnumId;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Instant getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(Instant expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getVerificationTokenTypeEnumId() {
		return verificationTokenTypeEnumId;
	}

	public void setVerificationTokenTypeEnumId(int verificationTokenTypeEnumId) {
		this.verificationTokenTypeEnumId = verificationTokenTypeEnumId;
	}
	
	public boolean isExpired() {
		return null != expirationDate && Instant.now().isBefore(expirationDate);
		
	}
}
