package com.tgpj.property.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ImageOrderRequest(@NotEmpty List<String> imageIds) {
}
