package io.a97lynk.oauth2aio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;

@SpringBootApplication
@EnableAuthorizationServer
//@EnableWebSecurity
//@EnableResourceServer
public class Oauth2AioApplication {

	public static void main(String[] args) {
//		AuthorizationEndpoint
		SpringApplication.run(Oauth2AioApplication.class, args);
	}

}
