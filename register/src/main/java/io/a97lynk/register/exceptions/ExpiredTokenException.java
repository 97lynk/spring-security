package io.a97lynk.register.exceptions;

import lombok.Data;

@Data
public class ExpiredTokenException extends Exception {

	private final String token;

	public ExpiredTokenException(String token) {
		this.token = token;
	}
}
