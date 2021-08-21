package io.a97lynk.register.controller;

import io.a97lynk.register.entity.PasswordResetToken;
import io.a97lynk.register.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/forgetPassword")
public class ForgetPasswordController {

	@Autowired
	UserService userService;

	@GetMapping
	public String page(Model model) {
		model.addAttribute("email", "");
		return "forgotPassword";
	}

	@PostMapping
	public String submit(@ModelAttribute("email") @Email String email, BindingResult bindingResult,
	                     HttpServletRequest request, Model model) {

		String token = UUID.randomUUID().toString();
		try {
			PasswordResetToken passwordResetToken = userService.createPasswordResetTokenForUser(email, token);
			CompletableFuture.runAsync(() -> {
				try {
					userService.sendMailResetPassword(request.getContextPath(), passwordResetToken.getUser(), token, "Forget password");
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			});
			model.addAttribute("success", true);
		} catch (UsernameNotFoundException exception) {
			model.addAttribute("success", false);
			bindingResult.reject("message.notExistEmail");
		}

		return "forgotPassword";
	}

//	@PostMapping("/changePassword")
}
