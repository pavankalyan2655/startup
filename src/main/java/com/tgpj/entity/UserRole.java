package com.tgpj.entity;

public enum UserRole {
	USER,
	ADMIN;

	public static UserRole normalize(String value) {
		if (value == null || value.isBlank()) {
			return USER;
		}
		String normalized = value.trim();
		switch (normalized.toUpperCase()) {
			case "USER":
			case "JOB_SEEKER":
			case "RECRUITER":
				return USER;
			case "ADMIN":
				return ADMIN;
			default:
				return USER;
		}
	}
}
