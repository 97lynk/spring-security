package io.a97lynk.register.controller;

import io.a97lynk.register.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FormLoginController {

	@Autowired
	UserService userService;

	@GetMapping("/loginPage")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/home")
	public ModelAndView homePage() {
		return new ModelAndView("index", "user", userService.getCurrentUser());
	}
}
