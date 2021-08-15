package io.a97lynk.register.controller;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.entity.VerificationToken;
import io.a97lynk.register.event.OnRegistrationCompleteEvent;
import io.a97lynk.register.exceptions.UserAlreadyExistException;
import io.a97lynk.register.service.IUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;

@Controller
@RequestMapping("/signup")
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
		userDto.setFirstName("Tuấn");
		userDto.setLastName("Nguyễn");
		userDto.setEmail("97lynk@gmail.com");
		userDto.setPassword("123");
		userDto.setMatchingPassword("123");
		model.addAttribute("user", userDto);
		return "registration";
	}

	@PostMapping
	public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto,
	                                        HttpServletRequest request, Errors errors) {
		try {
			User registered = userService.registerNewUserAccount(userDto);

			String appUrl = request.getContextPath();
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
					request.getLocale(), appUrl));
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
			return new ModelAndView("badUser", "messageKey", "auth.message.invalidToken");
		}

		User user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			return new ModelAndView("badUser", "messageKey", "auth.message.expired");
		}

		user.setEnabled(true);
		userService.saveRegisteredUser(user);
		return new ModelAndView("redirect:/loginPage", model);
	}
}
