package com.smart.util;

import org.springframework.stereotype.Component;

@Component
public interface EmailUtil {

	 void sendEmail(String toAddress, String subject, String text);

}
