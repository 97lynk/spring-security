package io.a97lynk.register.exceptions;

import lombok.Data;

@Data
public class NotFoundTokenException extends Exception {

	private final String token;

	public NotFoundTokenException(String token) {
		this.token = token;
	}
}
