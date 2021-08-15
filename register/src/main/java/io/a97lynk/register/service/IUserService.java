package io.a97lynk.register.service;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.entity.User;
import io.a97lynk.register.entity.VerificationToken;
import io.a97lynk.register.exceptions.UserAlreadyExistException;

public interface IUserService {

	User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;

	User getUser(String verificationToken);

	void saveRegisteredUser(User user);

	void createVerificationToken(User user, String token);

	VerificationToken getVerificationToken(String VerificationToken);
}
