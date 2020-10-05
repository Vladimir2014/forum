package com.ngtr.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ngtr.forum.model.Post;
import com.ngtr.forum.model.User;
import com.ngtr.forum.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findTopByPostAndUserOrderByIdDesc(Post post, User currentuser);

}
