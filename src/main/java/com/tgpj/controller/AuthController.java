package com.tgpj.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tgpj.dto.AuthResponse;
import com.tgpj.dto.LoginRequest;
import com.tgpj.dto.SignupRequest;
import com.tgpj.dto.SignupResponse;
import com.tgpj.dto.UserProfileResponse;
import com.tgpj.entity.User;
import com.tgpj.repository.UserRepository;
import com.tgpj.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final UserRepository userRepository;

	public AuthController(AuthService authService, UserRepository userRepository) {
		this.authService = authService;
		this.userRepository = userRepository;
	}

	@PostMapping({"/signup", "/register"})
	@ResponseStatus(HttpStatus.CREATED)
	public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
		return authService.signup(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@GetMapping("/me")
	public UserProfileResponse me(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));
		return new UserProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
	}

	@PostMapping("/logout")
	public Map<String, Object> logout() {
		return Map.of("success", true, "message", "Logged out successfully");
	}
}
