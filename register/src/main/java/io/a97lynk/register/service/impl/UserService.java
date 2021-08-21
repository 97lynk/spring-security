package io.a97lynk.register.service.impl;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.PasswordResetToken;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.entity.VerificationToken;
import io.a97lynk.register.exceptions.UserAlreadyExistException;
import io.a97lynk.register.repository.PasswordTokenRepository;
import io.a97lynk.register.repository.UserRepository;
import io.a97lynk.register.repository.VerificationTokenRepository;
import io.a97lynk.register.service.IUserService;
import io.a97lynk.register.service.MailService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.util.*;

@Service
@Transactional
public class UserService implements IUserService {

	private final UserRepository repository;

	private final VerificationTokenRepository tokenRepository;

	private final PasswordEncoder passwordEncoder;

	private final MessageSource messages;


	private final MailService mailService;


	private final PasswordTokenRepository passwordTokenRepository;

	public UserService(UserRepository repository, VerificationTokenRepository tokenRepository, PasswordEncoder passwordEncoder,
	                   MessageSource messages, TemplateEngine templateEngine, JavaMailSender mailSender, MailService mailService, PasswordTokenRepository passwordTokenRepository) {
		this.repository = repository;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.messages = messages;
		this.mailService = mailService;
		this.passwordTokenRepository = passwordTokenRepository;
	}

	@Override
	public User findUserByEmail(String email) throws UsernameNotFoundException {
		return repository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
	}

	@Override
	public PasswordResetToken createPasswordResetTokenForUser(String email, String token) throws UsernameNotFoundException {
		PasswordResetToken myToken = new PasswordResetToken();
		myToken.setToken(token);
		myToken.setExpiryDate(calculateExpiryDate(10));
		myToken.setUser(findUserByEmail(email));
		return passwordTokenRepository.save(myToken);
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
				.expiryDate(calculateExpiryDate(1))
				.user(user)
				.build();
		tokenRepository.save(myToken);
	}

	@Override
	public VerificationToken getVerificationToken(String token) {
		return tokenRepository.findByToken(token);
	}

	@Override
	public PasswordResetToken getPasswordResetToken(String token) {
		return passwordTokenRepository.findByToken(token);
	}

	@Override
	public VerificationToken generateNewVerificationToken(String existingToken) throws Exception {
		VerificationToken myToken = tokenRepository.findByToken(existingToken);
		if (myToken == null) throw new Exception("Invalid token");

		myToken.setExpiryDate(calculateExpiryDate(0));
		tokenRepository.save(myToken);


		VerificationToken myToken2 = VerificationToken.builder()
				.token(UUID.randomUUID().toString())
				.expiryDate(calculateExpiryDate(1))
				.user(myToken.getUser())
				.build();
		return tokenRepository.save(myToken2);
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