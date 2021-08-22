package io.a97lynk.register.event;

import io.a97lynk.register.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

	private String appUrl;
	private User user;

	public OnRegistrationCompleteEvent(User user, String appUrl) {
		super(user);

		this.user = user;
		this.appUrl = appUrl;
	}
}
