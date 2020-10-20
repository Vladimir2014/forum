package com.ngtr.forum.exception;

public class EnumNotFoundException  extends RuntimeException {
	public EnumNotFoundException(String message) {
		super(message);
	}
	
	public EnumNotFoundException(String message, Exception e) {
		super(message, e);
	}
}