package io.a97lynk.register.exceptions;

import lombok.Data;

@Data
public class UserAlreadyExistException extends Exception {

	private String message;

	public UserAlreadyExistException(String s) {
		message = s;
	}
}
