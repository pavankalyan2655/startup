package com.tgpj.service;

public class EmailAlreadyExistsException extends RuntimeException {

	public EmailAlreadyExistsException() {
		super("An account with this email already exists");
	}
}
