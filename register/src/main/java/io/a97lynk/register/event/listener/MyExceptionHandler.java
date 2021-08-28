package io.a97lynk.register.event.listener;

import io.a97lynk.register.exceptions.ExpiredTokenException;
import io.a97lynk.register.exceptions.NotFoundTokenException;
import org.springframework.context.MessageSource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.mail.MessagingException;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

	private final MessageSource messages;

	public MyExceptionHandler(MessageSource messages) {
		this.messages = messages;
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFound(RuntimeException ex, WebRequest request) {
		logger.error("404 Status Code", ex);

		Pair<String, Object> bodyOfResponse = Pair.of(
				messages.getMessage("message.userNotFound", null, request.getLocale()),
				"UserNotFound");

		return handleExceptionInternal(
				ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(MailAuthenticationException.class)
	public ResponseEntity<Object> handleMail(RuntimeException ex, WebRequest request) {
		logger.error("500 Status Code", ex);
		Pair<String, Object> bodyOfResponse = Pair.of(
				messages.getMessage("message.email.config.error", null, request.getLocale()),
				"MailError");

		return handleExceptionInternal(
				ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
		logger.error("500 Status Code", ex);

		Pair<String, Object> bodyOfResponse = Pair.of(
				messages.getMessage("message.error", null, request.getLocale()),
				"InternalError");

		return handleExceptionInternal(
				ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(NotFoundTokenException.class)
	public ModelAndView handleNotFoundTokenException(NotFoundTokenException ex) {
		logger.error("Not found token", ex);
		ModelMap model = new ModelMap()
				.addAttribute("expired", false)
				.addAttribute("token", ex.getToken())
				.addAttribute("messageKey", "auth.message.invalidToken");
		return new ModelAndView("badUser", model);
	}

	@ExceptionHandler(ExpiredTokenException.class)
	public ModelAndView handleExpiredTokenException(ExpiredTokenException ex) {
		logger.error("Expired token", ex);

		ModelMap model = new ModelMap()
				.addAttribute("expired", true)
				.addAttribute("token", ex.getToken())
				.addAttribute("messageKey", "auth.message.expired");
		return new ModelAndView("badUser", model);
	}

	@ExceptionHandler(MessagingException.class)
	public void handleMessagingException(MessagingException ex) {
		logger.error("Send mail error ", ex);
	}
}