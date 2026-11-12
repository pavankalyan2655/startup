package com.tgpj.controller;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tgpj.dto.WelcomeResponse;
import com.tgpj.entity.User;
import com.tgpj.repository.UserRepository;

@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {

	private final UserRepository userRepository;

	public WelcomeController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public WelcomeResponse welcome(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));
		return new WelcomeResponse("Welcome to Telangana Private Jobs!", user.getFullName(), user.getRole());
	}
}
