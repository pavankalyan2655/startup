package com.tgpj.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tgpj.dto.UserProfileResponse;
import com.tgpj.entity.User;
import com.tgpj.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/profile")
	public UserProfileResponse profile(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));
		return new UserProfileResponse(user.getFullName(), user.getEmail(), user.getRole());
	}
}
