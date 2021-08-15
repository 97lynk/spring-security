package io.a97lynk.register.exceptions;

public class UserAlreadyExistException extends Exception {

	private String message;

	public UserAlreadyExistException(String s) {
		message = s;
	}
}
