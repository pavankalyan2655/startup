package com.tgpj.dto;

public record AuthResponse(String accessToken, UserSummary user) {
}
