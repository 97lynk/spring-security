package io.a97lynk.oauth2aio;

import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@FrameworkEndpoint
@SessionAttributes("authorizationRequest")
public class CustomApprovalPage {

	/**
	 * Customize approval page flow @{link {@link WhitelabelApprovalEndpoint}}
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/oauth/confirm")
	public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
		Map<String, String> scopes = (Map<String, String>) (model.containsKey("scopes") ? model.get("scopes") : request
				.getAttribute("scopes"));

		model.put("scopes", scopes);
		return  "approval";
	}
}
