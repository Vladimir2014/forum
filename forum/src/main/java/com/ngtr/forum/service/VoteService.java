package com.ngtr.forum.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngtr.forum.dto.VoteDto;
import com.ngtr.forum.dtoMapper.VoteDtoMapper;
import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.model.Post;
import com.ngtr.forum.model.User;
import com.ngtr.forum.model.Vote;
import com.ngtr.forum.model.VoteType;
import com.ngtr.forum.repository.PostRepository;
import com.ngtr.forum.repository.VoteRepository;

@Service
public class VoteService {

	@Autowired
	private VoteRepository voteRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private AuthService authService;

	@Transactional
	public Vote save(VoteDto voteDto) {
		Vote vote = VoteDtoMapper.mapToVote(voteDto);
		Post post = postRepository.findById(voteDto.getPostId())
				.orElseThrow(() -> new ForumException("Cannot find post for id: " + voteDto.getPostId()));
		
		User currentUser = authService.getCurrentUser();
		
		Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByIdDesc(post, currentUser);
		
		if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
			throw new ForumException("You have already voted " + voteDto.getVoteType());
		}
		
		if (VoteType.UPVOTE.equals(vote.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		} else {
			post.setVoteCount(post.getVoteCount() - 1);
		}
		
		vote.setPost(post);		
		vote.setUser(currentUser);
		
		vote.setId(voteRepository.save(vote).getId());
		
		return vote;
	}
	
}
