package com.ngtr.forum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailContentBuilder {

	@Autowired
	private TemplateEngine templateEngine;
	
	public String build(String message) {
		Context context = new Context();
		context.setVariable("message", message);
		
		return templateEngine.process("mailTemplate", context);
	}
}
