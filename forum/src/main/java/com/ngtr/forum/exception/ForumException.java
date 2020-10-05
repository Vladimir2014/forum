package com.ngtr.forum.exception;

public class ForumException extends RuntimeException {
	public ForumException(String message) {
		super(message);
		System.out.println(message);
	}
}
