package com.tgpj.dto;

import com.tgpj.entity.UserRole;

public record SignupResponse(String message, String email, UserRole role) {
}
