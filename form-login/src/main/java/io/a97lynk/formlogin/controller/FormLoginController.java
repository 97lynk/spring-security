package io.a97lynk.formlogin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FormLoginController {

	private final MessageSource messageSource;

	private String data = "";

	@Autowired
	public FormLoginController(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	// @formatter:off
	@GetMapping
	public String indexPage(Model model) {
		model.addAttribute("data", this.data);
		return "index";
	}

	@GetMapping("/loginPage")
	public String loginPage() { return "login"; }

	@GetMapping("/admin/hello")
	public String adminPage() { return "adminPage"; }

	@GetMapping("/user/hello")
	public String userPage() { return "userPage"; }

	@PostMapping("/user/xssDemo")
	public String xssDemo(@RequestParam(name = "note") String data) {
		this.data = data;
		return "redirect:/";
	}
	//@formatter:on
}
