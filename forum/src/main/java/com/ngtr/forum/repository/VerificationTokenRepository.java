package com.ngtr.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ngtr.forum.model.User;
import com.ngtr.forum.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	public Optional<VerificationToken> findByToken(String token);
	
	public Optional<VerificationToken> findByTokenAndVerificationTokenTypeEnumId(String token, int verificationTokenTypeEnumId);
	
	@Transactional
	@Modifying
	@Query(value="DELETE FROM VerificationToken token WHERE token.user = ?1 and token.verificationTokenTypeEnumId = ?2")
	public void deleteAllTokens(User user, Integer verificationTokenTypeEnumId);

}
