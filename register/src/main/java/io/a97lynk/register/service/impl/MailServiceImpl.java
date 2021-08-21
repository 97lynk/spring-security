package io.a97lynk.register.service.impl;

import io.a97lynk.register.service.MailService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

	private static final String MAIL_TEMPLATE_DIR = "mail/";

	private final MessageSource messages;

	private final TemplateEngine templateEngine;

	private final JavaMailSender mailSender;

	public MailServiceImpl(MessageSource messages, TemplateEngine templateEngine, JavaMailSender mailSender) {
		this.messages = messages;
		this.templateEngine = templateEngine;
		this.mailSender = mailSender;
	}


	@Override
	public MimeMessage buildAndSendMail(String template, String to, String subject, Map<String, Object> props) throws MessagingException {
		MimeMessage mimeMessage = build(template, to, subject, props);
		mailSender.send(mimeMessage);
		return mimeMessage;
	}

	@Override
	public MimeMessage build(String template, String to, String subject, Map<String, Object> props) throws MessagingException {
		final Context context = new Context();
		context.setLocale(LocaleContextHolder.getLocale());
		context.setVariables(props);

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setSubject(subject);
		helper.setFrom("system");
		helper.setTo(to);

		helper.setText(templateEngine.process(MAIL_TEMPLATE_DIR + template, context), true);

		mailSender.send(mimeMessage);

		return mimeMessage;
	}
}
