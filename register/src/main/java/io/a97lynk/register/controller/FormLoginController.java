package io.a97lynk.register.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FormLoginController {

	@GetMapping("/loginPage")
	public String loginPage() {
		return "login";
	}
}
