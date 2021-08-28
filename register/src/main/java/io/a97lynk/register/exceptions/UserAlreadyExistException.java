package io.a97lynk.register.exceptions;

import lombok.Data;

@Data
public class UserAlreadyExistException extends Exception {

	private final String message;

	public UserAlreadyExistException(String message) {
		this.message = message;
	}
}
