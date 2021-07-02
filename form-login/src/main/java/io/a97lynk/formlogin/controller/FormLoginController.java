package io.a97lynk.formlogin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FormLoginController {

	private final MessageSource messageSource;

	@Autowired
	public FormLoginController(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	// @formatter:off
	@GetMapping
	public String indexPage() { return "index"; }

	@GetMapping("/loginPage")
	public String loginPage() { return "login"; }

	@GetMapping("/admin/hello")
	public String adminPage() { return "adminPage"; }

	@GetMapping("/user/hello")
	public String userPage() { return "userPage"; }
	//@formatter:on
}
