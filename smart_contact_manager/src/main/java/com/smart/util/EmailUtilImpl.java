package com.smart.util;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtilImpl implements EmailUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);

	@Autowired
	private JavaMailSender sender; // the dependency we've added
									// in pom.xml

	@Override
	public void sendEmail(String toAddress, String subject, String text) {
		// (mime)----->> Multi-purpose Internet mail extension
		MimeMessage message = sender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(text);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception in generate email" + e.getMessage());
		}
		sender.send(message);
	}

}
