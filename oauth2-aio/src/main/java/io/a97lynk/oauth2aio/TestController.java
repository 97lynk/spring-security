package io.a97lynk.oauth2aio;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TestController {

	HttpSessionRequestCache cache = new HttpSessionRequestCache();

	@GetMapping("/data")
	@ResponseBody
	public Authentication getAuth(Authentication authentication) {
		return authentication;
	}


	@GetMapping("/loginPage")
	public String loginPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		SavedRequest savedRequest = cache.getRequest(request, response);
		if (savedRequest != null)
			model.addAttribute("clientId", savedRequest.getParameterMap().getOrDefault("client_id", null)[0]);
		return "login";
	}
}
