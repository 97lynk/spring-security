package io.a97lynk.register.controller;

import io.a97lynk.register.dto.GenericResponse;
import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.entity.VerificationToken;
import io.a97lynk.register.event.OnRegistrationCompleteEvent;
import io.a97lynk.register.exceptions.UserAlreadyExistException;
import io.a97lynk.register.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/signup")
@Slf4j
public class RegisterController {

	private final IUserService userService;

	private final ApplicationEventPublisher eventPublisher;


	public RegisterController(IUserService userService, ApplicationEventPublisher eventPublisher) {
		this.userService = userService;
		this.eventPublisher = eventPublisher;
	}

	@GetMapping
	public String showRegistrationForm(Model model) {
		UserDto userDto = new UserDto();
//		userDto.setFirstName("Tuấn");
//		userDto.setLastName("Nguyễn");
//		userDto.setEmail("97lynk@gmail.com");
//		userDto.setPassword("123");
//		userDto.setMatchingPassword("123");
		model.addAttribute("user", userDto);
		return "registration";
	}

	@PostMapping
	public ModelAndView registerUserAccount(@ModelAttribute("user") @Validated UserDto userDto, BindingResult result, Errors errors,
	                                        HttpServletRequest request) {
		if (result.hasErrors()) {
			return new ModelAndView("registration");
		}

		try {
			User registered = userService.registerNewUserAccount(userDto);

			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), request.getContextPath()));
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
	public ModelAndView confirmRegistration(WebRequest request, ModelMap model, @RequestParam("token") String token) {

		VerificationToken verificationToken = userService.getVerificationToken(token);
		if (verificationToken == null) {
			model.addAttribute("expired", false);
			model.addAttribute("token", token);
			model.addAttribute("messageKey", "auth.message.invalidToken");
			return new ModelAndView("badUser", model);
		}

		User user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			model.addAttribute("expired", true);
			model.addAttribute("token", token);
			model.addAttribute("messageKey", "auth.message.expired");
			return new ModelAndView("badUser", model);
		}

		user.setEnabled(true);
		userService.saveRegisteredUser(user);
		return new ModelAndView("redirect:/loginPage", model);
	}

	@GetMapping("/resend")
	@ResponseBody
	public GenericResponse resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String existingToken) throws Exception {

		VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
		User user = userService.getUser(newToken.getToken());

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
