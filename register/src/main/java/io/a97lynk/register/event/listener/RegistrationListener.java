package io.a97lynk.register.event.listener;

import io.a97lynk.register.entity.User;
import io.a97lynk.register.event.OnRegistrationCompleteEvent;
import io.a97lynk.register.service.IUserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final IUserService service;

	private final MessageSource messages;

	private final JavaMailSender mailSender;

	public RegistrationListener(IUserService service, MessageSource messages, JavaMailSender mailSender) {
		this.service = service;
		this.messages = messages;
		this.mailSender = mailSender;
	}

	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		this.confirmRegistration(event);
	}

	private void confirmRegistration(OnRegistrationCompleteEvent event) {
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		service.createVerificationToken(user, token);

		String recipientAddress = user.getEmail();
		String subject = "Registration Confirmation";
		String confirmationUrl = event.getAppUrl() + "/signup/confirm?token=" + token;
		String message = messages.getMessage("message.regSuccess", null, event.getLocale());

		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom("system.97lynk@gmail.com");
		email.setTo(recipientAddress);
		email.setSubject(subject);
		email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
		mailSender.send(email);
	}


}