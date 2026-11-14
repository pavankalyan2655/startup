package com.tgpj.dto;

import com.tgpj.entity.UserRole;

public record WelcomeResponse(String message, String fullName, String email, UserRole role) {
}
