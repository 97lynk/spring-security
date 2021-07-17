package io.a97lynk.oauth2aio;

import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@FrameworkEndpoint
@SessionAttributes("authorizationRequest")
public class CustomApprovalPage {

	private static final String SCOPES = "scopes";

	/**
	 * Customize approval page flow @{link {@link WhitelabelApprovalEndpoint}}
	 *
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/oauth/confirm")
	public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) {
		Object scopes = model.getOrDefault(SCOPES, request.getAttribute(SCOPES));
		model.put(SCOPES, scopes);
		return "approval";
	}
}
