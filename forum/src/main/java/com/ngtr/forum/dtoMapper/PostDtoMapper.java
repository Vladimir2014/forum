package com.ngtr.forum.dtoMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.ngtr.forum.dto.PostDto;
import com.ngtr.forum.model.Post;
import com.ngtr.forum.model.VoteType;
import com.ngtr.forum.service.PostService;

@Service
public class PostDtoMapper {
	@Autowired
	private PostService postService;
	
	public Post toPost(PostDto postDto) {
		Post post = new Post();
		post.setPostName(postDto.getPostName());
		post.setDescription(postDto.getDescription());
		post.setUrl(postDto.getUrl());
		post.setVoteCount(0);
		
		return post;
	}
	
	public PostDto toPostDto(Post post) {
		PostDto postDto = new PostDto();
		
		postDto.setPostId(post.getPostId());
		postDto.setPostName(post.getPostName());
		postDto.setDescription(post.getDescription());
		postDto.setUrl(post.getUrl());
		postDto.setForumName(post.getForum().getName());
		postDto.setDuration(TimeAgo.using(post.getCreatedOn().toEpochMilli()));
		postDto.setVoteCount(post.getVoteCount());
		postDto.setUserName(post.getUser().getUsername());
		postDto.setCommnetCount(post.getComments().size());
		postDto.setPostUpVoted(postService.checkVoteType(post, VoteType.UPVOTE));
		postDto.setPostDownVoted(postService.checkVoteType(post, VoteType.DOWNVOTE));
		
		return postDto;
	}
}
