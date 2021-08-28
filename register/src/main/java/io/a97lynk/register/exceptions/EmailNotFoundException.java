package io.a97lynk.register.exceptions;

import lombok.Data;

@Data
public class EmailNotFoundException extends Exception {

	private final String message;

	public EmailNotFoundException(String message) {
		this.message = message;
	}
}
