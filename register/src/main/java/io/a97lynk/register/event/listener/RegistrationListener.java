package io.a97lynk.register.event.listener;

import io.a97lynk.register.entity.Token;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.event.OnRegistrationCompleteEvent;
import io.a97lynk.register.exceptions.EmailNotFoundException;
import io.a97lynk.register.service.TokenService;
import io.a97lynk.register.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final UserService service;
	private final TokenService tokenService;

	public RegistrationListener(UserService service, TokenService tokenService) {
		this.service = service;
		this.tokenService = tokenService;
	}

	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		try {
			this.confirmRegistration(event);
		} catch (EmailNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void confirmRegistration(OnRegistrationCompleteEvent event) throws EmailNotFoundException {
		User user = event.getUser();
		Token token = tokenService.createVerificationToken(user.getEmail());

		CompletableFuture.runAsync(() -> {
			try {
				service.sendMail(event.getAppUrl(), user, token.getToken(), "Registration Confirmation");
				log.info("Mail is sent to " + user.getEmail());
			} catch (MessagingException e) {
				log.error(e.getMessage());
			}
		});
	}


}