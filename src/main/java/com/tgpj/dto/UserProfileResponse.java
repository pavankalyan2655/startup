package com.tgpj.dto;

import com.tgpj.entity.UserRole;

public record UserProfileResponse(String fullName, String email, UserRole role) {
}
