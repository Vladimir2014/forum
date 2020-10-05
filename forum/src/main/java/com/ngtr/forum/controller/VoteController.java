package com.ngtr.forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ngtr.forum.dto.VoteDto;
import com.ngtr.forum.model.Vote;
import com.ngtr.forum.service.VoteService;

@RestController
@RequestMapping("/api/vote/")
public class VoteController {

	@Autowired
	private VoteService voteService;
	
	@PostMapping()
	public ResponseEntity<Void> createVote(@RequestBody VoteDto voteDto) {
		voteService.save(voteDto);
		
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
}
