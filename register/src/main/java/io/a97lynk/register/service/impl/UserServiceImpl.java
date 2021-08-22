package io.a97lynk.register.service.impl;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.Token;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.exceptions.EmailNotFoundException;
import io.a97lynk.register.exceptions.NotFoundTokenException;
import io.a97lynk.register.exceptions.UserAlreadyExistException;
import io.a97lynk.register.repository.TokenRepository;
import io.a97lynk.register.repository.UserRepository;
import io.a97lynk.register.service.MailService;
import io.a97lynk.register.service.TokenService;
import io.a97lynk.register.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository repository;

	private final TokenRepository tokenRepository;

	private final PasswordEncoder passwordEncoder;

	private final MessageSource messages;


	private final MailService mailService;


	private final TokenRepository passwordTokenRepository;

	private final TokenService tokenService;

	public UserServiceImpl(UserRepository repository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder,
	                       MessageSource messages, TemplateEngine templateEngine, JavaMailSender mailSender, MailService mailService, TokenRepository passwordTokenRepository, TokenService tokenService) {
		this.repository = repository;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.messages = messages;
		this.mailService = mailService;
		this.passwordTokenRepository = passwordTokenRepository;
		this.tokenService = tokenService;
	}

	@Override
	public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
		if (repository.existsByEmail(userDto.getEmail()))
			throw new UserAlreadyExistException(userDto.getEmail());

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
	public Token generateNewVerificationToken(String existingToken) throws NotFoundTokenException, EmailNotFoundException {
		Token myToken = tokenService.getTokenIgnoreExpired(existingToken);

		myToken.setExpiryDate(calculateExpiryDate(0));
		tokenRepository.save(myToken);

		return tokenService.createVerificationToken(myToken.getUser().getEmail());
	}

	@Override
	public void saveRegisteredUser(User user) {
		repository.save(user);
	}

	@Override
	public void sendMail(String contextPath, User user, String token, String subject) throws MessagingException {
		String confirmationUrl = String.format("http://localhost:8080%s/signup/confirm?token=%s", contextPath, token);
		Map<String, Object> props = new HashMap<>();
		props.put("url", confirmationUrl);
		props.put("fullname", String.format("%s %s", user.getFirstName(), user.getLastName()));

		mailService.buildAndSendMail("mail", user.getEmail(), subject, props);
	}

	@Override
	public void sendMailResetPassword(String contextPath, User user, String token, String subject) throws MessagingException {
		String confirmationUrl = String.format("http://localhost:8080%s/forgetPassword/changePassword?token=%s", contextPath, token);

		Map<String, Object> props = new HashMap<>();
		props.put("url", confirmationUrl);
		props.put("fullname", String.format("%s %s", user.getFirstName(), user.getLastName()));

		mailService.buildAndSendMail("mail", user.getEmail(), subject, props);
	}

	@Override
	public void changePassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
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