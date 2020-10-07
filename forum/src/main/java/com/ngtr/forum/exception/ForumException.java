package com.ngtr.forum.exception;

public class ForumException extends RuntimeException {
	public ForumException(String message) {
		super(message);
	}
	
	public ForumException(String message, Exception e) {
		super(message, e);
	}
}
