package io.a97lynk.formlogin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

//@Component
public class SecAuthSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {


	private final LoginAttemptService loginAttemptService;

	private final HttpServletRequest request;

	@Autowired
	public SecAuthSuccessEventListener(LoginAttemptService loginAttemptService, HttpServletRequest request) {
		this.loginAttemptService = loginAttemptService;
		this.request = request;
	}

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
		final String xHeader = request.getHeader(" X-Forwarded-For");
		if (xHeader == null)
			loginAttemptService.attemptSuccess(request.getRemoteAddr());
		else
			loginAttemptService.attemptSuccess(xHeader);
	}



}
