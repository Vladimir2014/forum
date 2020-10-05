package com.ngtr.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ngtr.forum.model.Comment;
import com.ngtr.forum.model.Post;
import com.ngtr.forum.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	public List<Comment> findByPost(Post post);
	
	public List<Comment> findByUser(User user);
}
