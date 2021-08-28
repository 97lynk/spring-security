package io.a97lynk.register.controller;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.Token;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.event.OnRegistrationCompleteEvent;
import io.a97lynk.register.exceptions.EmailNotFoundException;
import io.a97lynk.register.exceptions.ExpiredTokenException;
import io.a97lynk.register.exceptions.NotFoundTokenException;
import io.a97lynk.register.exceptions.UserAlreadyExistException;
import io.a97lynk.register.service.TokenService;
import io.a97lynk.register.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/signup")
@Slf4j
public class RegisterController {

	private static final String REGISTRATION_VIEW = "registration";
	private static final String USER_MODEL = "user";

	private final UserService userService;

	private final TokenService tokenService;

	private final ApplicationEventPublisher eventPublisher;


	public RegisterController(UserService userService, TokenService tokenService, ApplicationEventPublisher eventPublisher) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.eventPublisher = eventPublisher;
	}

	@GetMapping
	public ModelAndView registrationPage() {
		return new ModelAndView(REGISTRATION_VIEW, USER_MODEL, new UserDto());
	}

	@PostMapping
	public ModelAndView submitRegistration(@ModelAttribute(USER_MODEL) @Validated UserDto userDto, BindingResult result,
	                                       HttpServletRequest request) {
		if (result.hasErrors()) {
			return new ModelAndView(REGISTRATION_VIEW);
		}

		try {
			User registered = userService.registerNewUserAccount(userDto);

			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getContextPath()));
		} catch (UserAlreadyExistException uaeEx) {
			ModelAndView mav = new ModelAndView(REGISTRATION_VIEW, USER_MODEL, userDto);
			mav.addObject("message", "An account for that username/email already exists.");
			return mav;
		} catch (RuntimeException ex) {
			return new ModelAndView("emailError", USER_MODEL, userDto);
		}

		return new ModelAndView("successRegister", USER_MODEL, userDto);
	}

	@GetMapping("/confirm")
	public ModelAndView confirmRegistration(@RequestParam("token") String tokenStr) throws NotFoundTokenException, ExpiredTokenException {
		Token token = tokenService.getToken(tokenStr);

		User user = token.getUser();
		user.setEnabled(true);
		userService.saveRegisteredUser(user);
		return new ModelAndView("redirect:/loginPage");
	}

	@GetMapping("/resend")
	@ResponseBody
	public Pair<String, Object> resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String existingToken)
			throws NotFoundTokenException, EmailNotFoundException, MessagingException {

		Token newToken = userService.generateNewVerificationToken(existingToken);
		User user = newToken.getUser();

		userService.sendMail(request.getContextPath(), user, newToken.getToken(), "Resend Registration Confirmation");
		log.info("Mail is sent to " + user.getEmail());

		return Pair.of("message", "RESEND TOKEN");
	}
}
