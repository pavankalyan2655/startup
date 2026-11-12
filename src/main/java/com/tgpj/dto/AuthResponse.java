package com.tgpj.dto;

import com.tgpj.entity.UserRole;

public record AuthResponse(String token, String tokenType, String fullName, String email, UserRole role) {
}
