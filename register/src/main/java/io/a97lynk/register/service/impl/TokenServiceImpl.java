package io.a97lynk.register.service.impl;

import io.a97lynk.register.entity.Token;
import io.a97lynk.register.entity.Token.TokenType;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.exceptions.EmailNotFoundException;
import io.a97lynk.register.exceptions.ExpiredTokenException;
import io.a97lynk.register.exceptions.NotFoundTokenException;
import io.a97lynk.register.repository.TokenRepository;
import io.a97lynk.register.repository.UserRepository;
import io.a97lynk.register.service.TokenService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

	private final TokenRepository tokenRepository;
	private final UserRepository userRepository;

	public TokenServiceImpl(TokenRepository tokenRepository, UserRepository userRepository) {
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Token createVerificationToken(String email) throws EmailNotFoundException {
		return tokenRepository.save(buildToken(email, TokenType.VERIFICATION));
	}

	@Override
	public Token getToken(String tokenStr) throws NotFoundTokenException, ExpiredTokenException {
		Token token = this.getTokenIgnoreExpired(tokenStr);

		Calendar cal = Calendar.getInstance();
		if (token.getExpiryDate().before(cal.getTime())) {
			throw new ExpiredTokenException(tokenStr);
		}

		return token;
	}

	@Override
	public Token getTokenIgnoreExpired(String tokenStr) throws NotFoundTokenException {
		return tokenRepository.findByToken(tokenStr)
				.orElseThrow(() -> new NotFoundTokenException(tokenStr));
	}

	@Override
	public Token createPasswordToken(String email) throws EmailNotFoundException {
		return tokenRepository.save(buildToken(email, TokenType.PASSWORD));
	}

	private Token buildToken(String email, TokenType tokenType) throws EmailNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new EmailNotFoundException(email));

		return Token.builder()
				.token(UUID.randomUUID().toString())
				.type(tokenType)
				.expiryDate(calculateExpiryDate(1))
				.user(user)
				.build();
	}

	private Date calculateExpiryDate(int expiryTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(cal.getTime().getTime());
	}

}
