package com.ngtr.forum.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngtr.forum.dto.CommentDto;
import com.ngtr.forum.dtoMapper.CommentDtoMapper;
import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.model.Comment;
import com.ngtr.forum.model.NotificationEmail;
import com.ngtr.forum.model.Post;
import com.ngtr.forum.model.User;
import com.ngtr.forum.repository.CommentRepository;
import com.ngtr.forum.repository.PostRepository;
import com.ngtr.forum.repository.UserRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private MailContentBuilder mailContentBuilder;
	
	@Autowired
	private MailService mailService;

	@Transactional
	public CommentDto createComment(CommentDto commentDto) {
		Comment comment = CommentDtoMapper.mapToComment(commentDto);
		Post post = postService.findById(commentDto.getPostId());
		comment.setCreatedOn(Instant.now());
		comment.setUser(authService.getCurrentUser());
		comment.setPost(post);
		commentDto.setId(commentRepository.save(comment).getId());
		
		String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a commnet on your post. " + post.getUrl());
		
		sendCommnetNotification(message, post.getUser());
		
		return commentDto;
	}

	@Transactional(readOnly=true)
	public List<CommentDto> getAllCommentsForPost(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ForumException("Cannot find post : " + postId));
		
		List<Comment> comments = commentRepository.findByPost(post);
		
		return comments
		.stream()
		.map(CommentDtoMapper :: mapToCommentDto)
		.collect(Collectors.toList());
	}

	@Transactional(readOnly=true)
	public List<CommentDto> getCommentsByUserName(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new ForumException("Unknown user: " + username));
		
		List<Comment> comments = commentRepository.findByUser(user);
		
		return comments
		.stream()
		.map(CommentDtoMapper :: mapToCommentDto)
		.collect(Collectors.toList());
	}

	private void sendCommnetNotification(String message, User user) {
		mailService.sendMail(new NotificationEmail(user.getUsername() + " commented on your post", user.getEmail(), message));
	}
}
