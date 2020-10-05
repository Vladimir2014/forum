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

import com.ngtr.forum.dto.ForumDto;
import com.ngtr.forum.service.ForumService;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
	
	@Autowired
	private ForumService forumService;
	
	@PostMapping
	public ResponseEntity<ForumDto> createForum(@RequestBody ForumDto forumDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(forumService.save(forumDto));
	}
	
	@GetMapping
	public ResponseEntity<List<ForumDto>> getAllForums() {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(forumService.getAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ForumDto> getForum(@PathVariable Long id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(forumService.getForum(id));
	}
}
