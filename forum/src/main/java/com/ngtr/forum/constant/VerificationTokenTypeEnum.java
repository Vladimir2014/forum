package com.ngtr.forum.constant;

import com.ngtr.forum.exception.EnumNotFoundException;

public enum VerificationTokenTypeEnum {
	REGISTRATION(0),
	PASSWORD_RESET(1)
	;
	
	
	int id;
	
	VerificationTokenTypeEnum(int val) {
		id = val;
	}
	
	public void setId(int val ) {
		id = val;
	}
	
	public int getId() {
		return id;
	}
	
	public VerificationTokenTypeEnum forId(int id) {
		for (VerificationTokenTypeEnum token : values()) {
			if (token.getId() == id) {
				return token;
			}
		}
		
		throw new EnumNotFoundException("Cannot find VerificationTokenTypeEnum for id: " + id);
	}

}
