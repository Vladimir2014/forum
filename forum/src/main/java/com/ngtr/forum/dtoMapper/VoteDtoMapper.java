package com.ngtr.forum.dtoMapper;

import com.ngtr.forum.dto.VoteDto;
import com.ngtr.forum.model.Vote;

public class VoteDtoMapper {

	public static Vote mapToVote(VoteDto voteDto) {
		Vote vote = new Vote();
		
		vote.setVoteType(voteDto.getVoteType());
		
		return vote;
	}
	
	public static VoteDto mapToVoteDto(Vote vote) {
		VoteDto voteDto = new VoteDto();
		
		voteDto.setId(vote.getId());
		voteDto.setPostId(vote.getPost().getPostId());
		voteDto.setUserId(vote.getUser().getUserId());
		voteDto.setVoteType(vote.getVoteType());
		
		return voteDto;
	}

}
