package io.a97lynk.formlogin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

//@Component
public class SecAuthFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private final LoginAttemptService loginAttemptService;

	private final HttpServletRequest request;

	@Autowired
	public SecAuthFailureEventListener(LoginAttemptService loginAttemptService, HttpServletRequest request) {
		this.loginAttemptService = loginAttemptService;
		this.request = request;
	}

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) {
		final String xHeader = request.getHeader(" X-Forwarded-For");
		if (xHeader == null)
			loginAttemptService.attemptFailure(request.getRemoteAddr());
		else
			loginAttemptService.attemptFailure(xHeader);
	}
}
