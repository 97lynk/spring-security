package io.a97lynk.register.controller;

import io.a97lynk.register.dto.GenericResponse;
import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.Token;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.event.OnRegistrationCompleteEvent;
import io.a97lynk.register.exceptions.ExpiredTokenException;
import io.a97lynk.register.exceptions.NotFoundTokenException;
import io.a97lynk.register.exceptions.UserAlreadyExistException;
import io.a97lynk.register.service.TokenService;
import io.a97lynk.register.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/signup")
@Slf4j
public class RegisterController {

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
		return new ModelAndView("registration", "user", new UserDto());
	}

	@PostMapping
	public ModelAndView submitRegistration(@ModelAttribute("user") @Validated UserDto userDto, BindingResult result,
	                                       HttpServletRequest request) {
		if (result.hasErrors()) {
			return new ModelAndView("registration");
		}

		try {
			User registered = userService.registerNewUserAccount(userDto);

			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getContextPath()));
		} catch (UserAlreadyExistException uaeEx) {
			ModelAndView mav = new ModelAndView("registration", "user", userDto);
			mav.addObject("message", "An account for that username/email already exists.");
			return mav;
		} catch (RuntimeException ex) {
			return new ModelAndView("emailError", "user", userDto);
		}

		return new ModelAndView("successRegister", "user", userDto);
	}

	@GetMapping("/confirm")
	public ModelAndView confirmRegistration(WebRequest request, ModelMap model, @RequestParam("token") String tokenStr) {

		try {
			Token token = tokenService.getToken(tokenStr);

			User user = token.getUser();
			user.setEnabled(true);
			userService.saveRegisteredUser(user);
			return new ModelAndView("redirect:/loginPage", model);

		} catch (NotFoundTokenException e) {
			model.addAttribute("expired", false);
			model.addAttribute("token", tokenStr);
			model.addAttribute("messageKey", "auth.message.invalidToken");
			return new ModelAndView("badUser", model);
		} catch (ExpiredTokenException e) {
			model.addAttribute("expired", true);
			model.addAttribute("token", tokenStr);
			model.addAttribute("messageKey", "auth.message.expired");
			return new ModelAndView("badUser", model);
		}
	}

	@GetMapping("/resend")
	@ResponseBody
	public GenericResponse resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String existingToken) throws Exception {

		Token newToken = userService.generateNewVerificationToken(existingToken);
		User user = newToken.getUser();

		CompletableFuture.runAsync(() -> {
			try {
				userService.sendMail(request.getContextPath(), user, newToken.getToken(), "Resend Registration Confirmation");
				log.info("Mail is sent to " + user.getEmail());
			} catch (MessagingException e) {
				log.error(e.getMessage());
			}
		});

		return new GenericResponse("RESEND TOKEN");
	}
}
