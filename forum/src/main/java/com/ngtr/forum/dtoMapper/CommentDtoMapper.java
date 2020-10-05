package com.ngtr.forum.dtoMapper;

import com.ngtr.forum.dto.CommentDto;
import com.ngtr.forum.model.Comment;

public class CommentDtoMapper {

	public static Comment mapToComment(CommentDto commentDto) {
		Comment comment = new Comment();
		
		comment.setText(commentDto.getText());
		comment.setCreatedOn(commentDto.getCreatedOn());
		comment.setId(commentDto.getId());
		
		return comment;
	}
	
	public static CommentDto mapToCommentDto(Comment comment) {
		CommentDto commentDto = new CommentDto();
		
		commentDto.setId(comment.getId());
		commentDto.setPostId(comment.getPost().getPostId());
		commentDto.setText(comment.getText());
		commentDto.setUserId(comment.getUser().getUserId());
		
		return commentDto;
	}

}
