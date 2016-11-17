package com.servicelibre.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RessourceIntrouvableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9106692787036002247L;

	public RessourceIntrouvableException() {
		super();
	}

	public RessourceIntrouvableException(String message) {
		super(message);
	}

}
