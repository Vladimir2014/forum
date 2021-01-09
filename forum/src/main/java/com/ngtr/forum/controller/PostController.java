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

import com.ngtr.forum.dto.PostDto;
import com.ngtr.forum.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	@Autowired
	private PostService postService;
	
	@PostMapping
	public ResponseEntity<PostDto> createPost(@RequestBody PostDto postRequest) {
		PostDto post = postService.save(postRequest);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(post);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(postService.getPost(id));
	}
	
	@GetMapping()
	public ResponseEntity<List<PostDto>> getAllPosts() {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(postService.getAllPosts());
	}
	
	@GetMapping("/by-forum/{id}")
	public ResponseEntity<List<PostDto>> getPostsForForum(@PathVariable Long id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(postService.getPostsForForum(id));
	}
	
	@GetMapping("/by-user/{name}")
	public ResponseEntity<List<PostDto>> getPostsForUser(@PathVariable String name) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(postService.getPostsForUser(name));
	}
}
