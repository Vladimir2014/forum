package com.ngtr.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ngtr.forum.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	public Optional<VerificationToken> findByToken(String token);
	
	public Optional<VerificationToken> findByTokenAndVerificationTokenTypeEnumId(String token, int verificationTokenTypeEnumId);

}
