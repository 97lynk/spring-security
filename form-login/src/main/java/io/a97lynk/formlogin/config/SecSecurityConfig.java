package io.a97lynk.formlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

@Configuration
@EnableWebSecurity(debug = true)
public class SecSecurityConfig extends WebSecurityConfigurerAdapter {


	private final PersistentTokenRepository tokenRepository;


	public SecSecurityConfig(PersistentTokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;

	}


	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

		auth
				.inMemoryAuthentication()

				// normal user
				.withUser("user1").password(passwordEncoder().encode("user1Pass")).roles("USER")
				// disabled user
				.and()
				.withUser("user2").password(passwordEncoder().encode("user2Pass")).roles("USER").disabled(true)
				// disabled user
				.and()
				.withUser("user3").password(passwordEncoder().encode("user2Pass")).roles("USER").accountLocked(true)
				// normal admin
				.and()
				.withUser("admin").password(passwordEncoder().encode("adminPass")).roles("ADMIN");
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		// DefaultLoginPageGeneratingFilter.class -> when use default login page
		// UsernamePasswordAuthenticationFilter.class
		// DaoAuthenticationProvider.class
		http
				.cors() // Cross-Origin Resource Sharing
				.and()
				.csrf() // Cross-site Request Forgery
				.and()
				.headers()
				.xssProtection()
				.and()
				.frameOptions().disable()
				.and()
				.authorizeRequests()
				.antMatchers("/user/**").hasAnyRole(    "USER", "ADMIN")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/anonymous*").anonymous()
				.antMatchers("/login*", "/h2-console/**").permitAll()
				.anyRequest().authenticated()

				/// form login config
				.and()
				.formLogin()
				.loginPage("/loginPage")
				.loginProcessingUrl("/perform_login")

				// when success
				.defaultSuccessUrl("/", true) // ->  new SavedRequestAwareAuthenticationSuccessHandler()
//				.successForwardUrl() -> successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl))
//				.successHandler(new RefererAuthenticationSuccessHandler()) // -> after login success will redirect to url that defined in Referer header
//				.successHandler(new SavedRequestAwareAuthenticationSuccessHandler()) // -> successful login, users will be redirected to the URL saved in the original request
//				.successHandler(new ForwardAuthenticationSuccessHandler())
//				.successHandler(new SimpleUrlAuthenticationSuccessHandler())

				// when fail
				.failureUrl("/loginPage?status=error") // -> failureHandler(new SimpleUrlAuthenticationFailureHandler(url))
//				.failureForwardUrl() -> failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
//				.failureHandler(new DelegatingAuthenticationFailureHandler())
//				.failureHandler(new SimpleUrlAuthenticationFailureHandler())
//				.failureHandler(new ForwardAuthenticationFailureHandler())
//				.failureHandler(new ExceptionMappingAuthenticationFailureHandler())

				.and()
				.logout()
				.logoutUrl("/perform_logout")
				.logoutSuccessUrl("/loginPage?status=logout")
				.deleteCookies("JSESSIONID")
//				.logoutSuccessHandler(new DelegatingLogoutSuccessHandler())
//				.logoutSuccessHandler(new SimpleUrlLogoutSuccessHandler())
//				.logoutSuccessHandler(new ForwardLogoutSuccessHandler())
//				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()) // -> return status without redirect
//				.logoutSuccessHandler(logoutSuccessHandler())

				// remember me with token based
//				.and()
//				.rememberMe()
//				.rememberMeParameter("rememberMe")
//				.key("uniqueAndSecret")
//				.tokenValiditySeconds(86400)

				.and()
				.rememberMe()
				.rememberMeParameter("rememberMe")
				.key("rememberMeKey")
				.tokenRepository(tokenRepository)
				.tokenValiditySeconds(2000)
		;

		http.sessionManagement()
				.maximumSessions(1)
				.maxSessionsPreventsLogin(true)
				.expiredSessionStrategy(new SimpleRedirectSessionInformationExpiredStrategy("/loginPage"))
//				.invalidSessionUrl()
//				.expiredUrl()
		;


//		.expiredSessionStrategy(new ResponseBodySessionInformationExpiredStrategy())
//		SecurityContextPersistenceFilter
//  ----> UsernamePasswordAuthenticationFilter#doFilter#attemptAuthentication -> ProviderManager [RememberMeAuthenticationProvider, DaoAuthenticationProvider]
//  ----> RememberMeAuthenticationFilter
//  ----> AnonymousAuthenticationFilter
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}