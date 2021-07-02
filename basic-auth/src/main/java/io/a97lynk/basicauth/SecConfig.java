package io.a97lynk.basicauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * If this class isn't created, SpringBootWebSecurityConfiguration would used
 * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
 */
@Configuration
@EnableWebSecurity
public class SecConfig extends WebSecurityConfigurerAdapter {

//	@Autowired
//	private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
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
	protected void configure(HttpSecurity http) throws Exception {
//		 BasicAuthenticationFilter.class: validate token with user account
		http
				.authorizeRequests()
				.antMatchers("/home/**").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/user/**").hasRole("USER")
				.anyRequest().authenticated()

				/// basic login
				.and()
				.httpBasic()
//				.realmName("Username is incorrect")
//				.authenticationEntryPoint(authenticationEntryPoint) // custom
		;
//		ManagementWebSecurityAutoConfiguration
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
