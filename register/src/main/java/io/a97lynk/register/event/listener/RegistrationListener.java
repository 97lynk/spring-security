package io.a97lynk.register.event.listener;

import io.a97lynk.register.entity.User;
import io.a97lynk.register.event.OnRegistrationCompleteEvent;
import io.a97lynk.register.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final IUserService service;

	public RegistrationListener(IUserService service) {
		this.service = service;
	}

	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		this.confirmRegistration(event);
	}

	private void confirmRegistration(OnRegistrationCompleteEvent event) {
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		service.createVerificationToken(user, token);

		CompletableFuture.runAsync(() -> {
			try {
				service.sendMail(event.getAppUrl(), user, token, "Registration Confirmation");
				log.info("Mail is sent to " + user.getEmail());
			} catch (MessagingException e) {
				log.error(e.getMessage());
			}
		});
	}


}