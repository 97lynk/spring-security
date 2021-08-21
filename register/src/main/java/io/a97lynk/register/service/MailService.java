package io.a97lynk.register.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

public interface MailService {

	MimeMessage buildAndSendMail(String template, String to, String subject, Map<String, Object> props) throws MessagingException;

	MimeMessage build(String template, String to, String subject, Map<String, Object> props) throws MessagingException;
}
