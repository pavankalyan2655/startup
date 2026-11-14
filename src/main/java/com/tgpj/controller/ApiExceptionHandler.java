package com.tgpj.controller;

import java.time.Instant;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.tgpj.dto.ApiError;
import com.tgpj.service.EmailAlreadyExistsException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleEmailAlreadyExists(RuntimeException exception, WebRequest request) {
		return error(HttpStatus.CONFLICT, exception.getMessage(), request);
	}

	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleDuplicateKey(DuplicateKeyException exception, WebRequest request) {
		return error(HttpStatus.CONFLICT, "A record with the supplied unique value already exists", request);
	}

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiError handleBadCredentials(BadCredentialsException exception, WebRequest request) {
		return error(HttpStatus.UNAUTHORIZED, "Invalid email or password", request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiError handleAccessDenied(AccessDeniedException exception, WebRequest request) {
		return error(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleValidation(MethodArgumentNotValidException exception, WebRequest request) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.orElse("Request validation failed");
		return error(HttpStatus.BAD_REQUEST, message, request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleInvalidBody(HttpMessageNotReadableException exception, WebRequest request) {
		return error(HttpStatus.BAD_REQUEST, "Request body is missing or malformed", request);
	}

	private ApiError error(HttpStatus status, String message, WebRequest request) {
		String path = ((ServletWebRequest) request).getRequest().getRequestURI();
		return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path);
	}
}
