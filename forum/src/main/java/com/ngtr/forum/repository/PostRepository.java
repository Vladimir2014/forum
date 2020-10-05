package com.ngtr.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ngtr.forum.model.Forum;
import com.ngtr.forum.model.Post;
import com.ngtr.forum.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByForum(Forum forum);

	List<Post> findByUser(User user);

}
