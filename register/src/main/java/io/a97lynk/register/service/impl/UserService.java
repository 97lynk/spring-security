package io.a97lynk.register.service.impl;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.entity.VerificationToken;
import io.a97lynk.register.exceptions.UserAlreadyExistException;
import io.a97lynk.register.repository.UserRepository;
import io.a97lynk.register.repository.VerificationTokenRepository;
import io.a97lynk.register.service.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Service
@Transactional
public class UserService implements IUserService {

	private final UserRepository repository;

	private final VerificationTokenRepository tokenRepository;

	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository repository, VerificationTokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
		if (repository.existsByEmail(userDto.getEmail()))
			throw new UserAlreadyExistException(String.format("There is an account with that email address: %s", userDto.getEmail()));

		User user = User.builder()
				.firstName(userDto.getFirstName())
				.lastName(userDto.getLastName())
				.password(passwordEncoder.encode(userDto.getPassword()))
				.email(userDto.getEmail())
				.build();

		user.setRoles(Arrays.asList("ROLE_USER"));

		return repository.save(user);
	}

	@Override
	public User getUser(String token) {
		return tokenRepository.findByToken(token).getUser();
	}

	@Override
	public void createVerificationToken(User user, String token) {
		VerificationToken myToken = VerificationToken.builder()
				.token(token)
				.expiryDate(calculateExpiryDate(1440))
				.user(user)
				.build();
		tokenRepository.save(myToken);
	}

	@Override
	public VerificationToken getVerificationToken(String token) {
		return tokenRepository.findByToken(token);
	}

	@Override
	public void saveRegisteredUser(User user) {
		repository.save(user);
	}

	// TODO
	//	There are two opportunities for improvement in handling the VerificationToken checking and expiration scenarios:
	//
	//	We can use a Cron Job to check for token expiration in the background
	//	We can give the user the opportunity to get a new token once it has expired

	private Date calculateExpiryDate(int expiryTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(cal.getTime().getTime());
	}

}