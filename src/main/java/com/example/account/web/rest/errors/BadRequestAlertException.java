package com.example.account.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestAlertException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadRequestAlertException(String message) {
		super(message);
	}

}
