package com.tgpj.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tgpj.dto.AuthResponse;
import com.tgpj.dto.LoginRequest;
import com.tgpj.dto.SignupRequest;
import com.tgpj.dto.SignupResponse;
import com.tgpj.entity.User;
import com.tgpj.entity.UserRole;
import com.tgpj.repository.UserRepository;
import com.tgpj.security.JwtService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public SignupResponse signup(SignupRequest request) {
		String email = normalizeEmail(request.email());
		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyExistsException();
		}

		User user = new User(request.fullName().trim(), email,
				passwordEncoder.encode(request.password()), UserRole.JOB_SEEKER);
		try {
			userRepository.save(user);
		}
		catch (DuplicateKeyException exception) {
			throw new EmailAlreadyExistsException();
		}
		return new SignupResponse("Registration successful", user.getEmail(), user.getRole());
	}

	public AuthResponse login(LoginRequest request) {
		String email = normalizeEmail(request.email());
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, request.password()));
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));
		return new AuthResponse(jwtService.generateToken(user), "Bearer",
				user.getFullName(), user.getEmail(), user.getRole());
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(java.util.Locale.ROOT);
	}
}
