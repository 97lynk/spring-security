package io.a97lynk.oauth2aio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser("user1")
				.password("{noop}123")
				.roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors() // Cross-Origin Resource Sharing
				.and()
				.csrf() // Cross-site Request Forgery
				.and()
				.formLogin()
				.loginPage("/loginPage")
				.loginProcessingUrl("/perform_login")
				.failureUrl("/loginPage?error")
				.and()
				.authorizeRequests()
				.antMatchers("/login**").permitAll()
				.anyRequest().authenticated()
		;

		http.addFilterAfter(defaultLogoutPage(), UsernamePasswordAuthenticationFilter.class);
	}

	private OncePerRequestFilter defaultLogoutPage() {
		DefaultLogoutPageGeneratingFilter filter = new DefaultLogoutPageGeneratingFilter();
		filter.setResolveHiddenInputs(this::hiddenInputs);
		return filter;
	}

	private Map<String, String> hiddenInputs(HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		return (token != null) ? Collections.singletonMap(token.getParameterName(), token.getToken())
				: Collections.emptyMap();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean()
			throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web
				.ignoring()
				.antMatchers("/h2-console/**");
	}

}
