package com.ngtr.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ngtr.forum.model.Forum;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long>{
	public Optional<Forum> findByName(String name);

}
