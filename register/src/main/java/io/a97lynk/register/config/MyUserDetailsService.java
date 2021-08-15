package io.a97lynk.register.config;

import io.a97lynk.register.entity.User;
import io.a97lynk.register.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public MyUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + email));

		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPassword())
				.disabled(!user.isEnabled())
				.authorities(getAuthorities(user.getRoles()))
				.build();

	}

	private static List<GrantedAuthority> getAuthorities(List<String> roles) {
		return AuthorityUtils.createAuthorityList(roles.toArray(new String[0]));
	}
}
