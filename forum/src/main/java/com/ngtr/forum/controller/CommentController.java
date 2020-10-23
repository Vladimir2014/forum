package com.ngtr.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ngtr.forum.dto.CommentDto;
import com.ngtr.forum.service.CommentService;

@RestController
@RequestMapping("/api/comments/")
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	@PostMapping
	public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(commentService.createComment(commentDto));
				
	}
	
	@GetMapping("/by-post-id/{postId}")
	public ResponseEntity<List<CommentDto>> getAllCommentsForPost(@PathVariable Long postId) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(commentService.getAllCommentsForPost(postId));
	}
	
	@GetMapping("/by-user/{username}")
	public ResponseEntity<List<CommentDto>> getCommentsByUserName(@PathVariable String username) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(commentService.getCommentsByUserName(username));
	}
	
	
}
