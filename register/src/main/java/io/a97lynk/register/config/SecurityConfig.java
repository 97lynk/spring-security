package io.a97lynk.register.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final MyUserDetailsService userDetailsService;

	private final PasswordEncoder passwordEncoder;

	public SecurityConfig(MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder);
	}

	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/h2-console/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
//				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/login*", "/signup/**").permitAll()
				.anyRequest().authenticated()

				/// form login config
				.and()
				.formLogin()
				.loginPage("/loginPage")
				.loginProcessingUrl("/perform_login")

				// when success
				.defaultSuccessUrl("/", true) // ->  new SavedRequestAwareAuthenticationSuccessHandler()
				// when fail
				.failureUrl("/loginPage?status=error") // -> failureHandler(new SimpleUrlAuthenticationFailureHandler(url))

				.and()
				.logout()
				.logoutUrl("/perform_logout")
				.logoutSuccessUrl("/loginPage?status=logout")
				.deleteCookies("JSESSIONID");
	}
}
