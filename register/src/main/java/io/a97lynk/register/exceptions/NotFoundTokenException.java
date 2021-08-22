package io.a97lynk.register.exceptions;

import lombok.Data;

@Data
public class NotFoundTokenException extends Exception {

	private String message;

	public NotFoundTokenException(String message) {
		this.message = message;
	}
}
