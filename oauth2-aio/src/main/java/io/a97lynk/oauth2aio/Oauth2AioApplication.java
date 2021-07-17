package io.a97lynk.oauth2aio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;

@SpringBootApplication
//@EnableWebSecurity
//@EnableResourceServer
public class Oauth2AioApplication {

	private AuthorizationEndpoint authorizationEndpoint;

	public Oauth2AioApplication(AuthorizationEndpoint authorizationEndpoint) {
		this.authorizationEndpoint = authorizationEndpoint;
		this.authorizationEndpoint.setUserApprovalPage("forward:/oauth/confirm");
	}

	public static void main(String[] args) {
//		AuthorizationEndpoint
		SpringApplication.run(Oauth2AioApplication.class, args);
	}

}
