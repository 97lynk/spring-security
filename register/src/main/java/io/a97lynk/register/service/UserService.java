package io.a97lynk.register.service;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.Token;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.exceptions.EmailNotFoundException;
import io.a97lynk.register.exceptions.NotFoundTokenException;
import io.a97lynk.register.exceptions.UserAlreadyExistException;

import javax.mail.MessagingException;

public interface UserService {

	User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;

	void saveRegisteredUser(User user);

	Token generateNewVerificationToken(String existingToken) throws NotFoundTokenException, EmailNotFoundException;

	void sendMail(String contextPath, User user, String token, String subject) throws MessagingException;

	void sendMailResetPassword(String contextPath, User user, String token, String subject) throws MessagingException;

	void changePassword(User user, String password);
}
