package com.ngtr.forum.dtoMapper;

import com.ngtr.forum.dto.CurrentUserDetails;
import com.ngtr.forum.model.User;

public class CurrentUserDetailsDtoMapper {
	
	public static CurrentUserDetails toDto(User user) {
		CurrentUserDetails dto = new CurrentUserDetails();
		
		dto.setEmail(user.getEmail());
		dto.setUsername(user.getUsername());
		
		return dto;
	}

}
