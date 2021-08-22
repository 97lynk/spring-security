package io.a97lynk.register.controller;

import io.a97lynk.register.dto.ChangePasswordDto;
import io.a97lynk.register.entity.Token;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.exceptions.EmailNotFoundException;
import io.a97lynk.register.exceptions.ExpiredTokenException;
import io.a97lynk.register.exceptions.NotFoundTokenException;
import io.a97lynk.register.service.TokenService;
import io.a97lynk.register.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.concurrent.CompletableFuture;


@Controller
@RequestMapping("/forgetPassword")
public class ForgetPasswordController {

	private final UserService userService;
	private final TokenService tokenService;

	public ForgetPasswordController(UserService userService, TokenService tokenService) {
		this.userService = userService;
		this.tokenService = tokenService;
	}

	@GetMapping
	public String page(Model model) {
		model.addAttribute("email", "");
		return "forgotPassword";
	}

	@PostMapping
	public String submit(@ModelAttribute("email") @Email String email, BindingResult bindingResult,
	                     HttpServletRequest request, Model model) {

		try {
			Token token = tokenService.createPasswordToken(email);
			CompletableFuture.runAsync(() -> {
				try {
					userService.sendMailResetPassword(
							request.getContextPath(),
							token.getUser(),
							token.getToken(),
							"Forget password");
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			});
			model.addAttribute("success", true);
		} catch (EmailNotFoundException exception) {
			model.addAttribute("success", false);
			bindingResult.reject("message.notExistEmail");
		}

		return "forgotPassword";
	}

	@GetMapping("/changePassword")
	public ModelAndView changePasswordPage(@RequestParam("token") String tokenStr, ModelMap model) {

		try {
			Token token = tokenService.getToken(tokenStr);
			User user = token.getUser();

			ChangePasswordDto dto = new ChangePasswordDto();
			dto.setEmail(user.getEmail());
			dto.setPassword("");
			dto.setMatchingPassword("");
			dto.setToken(tokenStr);
			model.addAttribute("user", dto);
			return new ModelAndView("changePassword", model);
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

	@PostMapping("/changePassword")
	public ModelAndView changePassword(@ModelAttribute("user") @Valid ChangePasswordDto dto, BindingResult result, ModelMap model) {
		if (result.hasErrors()) return new ModelAndView("changePassword");

		try {
			Token token = tokenService.getToken(dto.getToken());
			User user = token.getUser();
			userService.changePassword(user, dto.getPassword());

			return new ModelAndView("redirect:/loginPage");
		} catch (NotFoundTokenException e) {
			model.addAttribute("expired", false);
			model.addAttribute("token", dto.getToken());
			model.addAttribute("messageKey", "auth.message.invalidToken");
			return new ModelAndView("badUser", model);
		} catch (ExpiredTokenException e) {
			model.addAttribute("expired", true);
			model.addAttribute("token", dto.getToken());
			model.addAttribute("messageKey", "auth.message.expired");
			return new ModelAndView("badUser", model);
		}

	}
}
