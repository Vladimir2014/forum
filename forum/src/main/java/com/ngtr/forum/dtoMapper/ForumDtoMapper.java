package com.ngtr.forum.dtoMapper;

import com.ngtr.forum.dto.ForumDto;
import com.ngtr.forum.model.Forum;

public class ForumDtoMapper {
	
	public static ForumDto mapToDto(Forum forum) {
		ForumDto forumDto = new ForumDto();
		forumDto.setName(forum.getName());
		forumDto.setDescription(forum.getDescription());
		forumDto.setId(forum.getId());
		forumDto.setNumberOfPosts(forum.getPosts().size());
		
		return forumDto;
	}
	
	public static Forum mapForumDto(ForumDto forumDto) {
		Forum forum = new Forum();
		forum.setName(forumDto.getName());
		forum.setDescription(forumDto.getDescription());
		
		return forum;		
	}

}
