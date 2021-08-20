package io.a97lynk.register.service;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.entity.VerificationToken;
import io.a97lynk.register.exceptions.UserAlreadyExistException;

import javax.mail.MessagingException;

public interface IUserService {

	User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;

	User getUser(String verificationToken);

	void saveRegisteredUser(User user);

	void createVerificationToken(User user, String token);

	VerificationToken getVerificationToken(String VerificationToken);

	VerificationToken generateNewVerificationToken(String existingToken) throws Exception;

	void sendMail(String contextPath, User user, String token, String subject) throws MessagingException;
}
