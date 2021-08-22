package io.a97lynk.register.exceptions;

import lombok.Data;

@Data
public class ExpiredTokenException extends Exception {

	private String message;

	public ExpiredTokenException(String message) {
		this.message = message;
	}
}
