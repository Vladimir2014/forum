package com.ngtr.forum.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.model.NotificationEmail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailService {
	Logger logger = LogManager.getLogger(MailService.class);

	@Value("${from-email-address}")
	private String fromEmail;
	private TemplateEngine templateEngine;
	
	@Autowired
	public MailService(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailContentBuilder mailContentBuilder;
	

	@Async
	public void sendMail(NotificationEmail notificationEmail) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(fromEmail);
			messageHelper.setTo(notificationEmail.getRecipient());
			messageHelper.setSubject(notificationEmail.getSubject());
			messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
		};
		
		try {
			mailSender.send(messagePreparator);
			logger.info("Activation email sent!!!");
		} catch (MailException me) {
			throw new ForumException("Exception occured while trying to send mail");
		}
	}
	
	public void sendActivationEmail(String email, String token) {
		sendMail(new NotificationEmail("Please Activate your account",
				 					   email,
				 					   "http://localhost:8081/api/auth/accountVerification/" + token));
	}
	
	public void sendPasswordResetConfirmationCodeEmail(String username, String email, String code) throws MessagingException {
		Context context = new Context();
		context.setVariable("code", code);
		context.setVariable("username", username);
		
		// Prepare message using a Spring helper
	    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
	    final MimeMessageHelper message =
	        new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
	    message.setSubject("Password Reset Code");
	    message.setFrom(fromEmail);
	    message.setTo(email);

	    // Create the HTML body using Thymeleaf
	    final String htmlContent = this.templateEngine.process("passwordCodeEmailTemplate", context);
	    message.setText(htmlContent, true); // true = isHtml

	    // Send mail
	    this.mailSender.send(mimeMessage);
	}
}
