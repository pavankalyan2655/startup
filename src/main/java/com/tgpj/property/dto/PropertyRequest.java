package com.tgpj.property.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PropertyRequest(
		@NotBlank @Size(max = 200) String title,
		@NotBlank @Size(max = 200) String projectName,
		@NotBlank @Size(max = 150) String builder,
		@NotBlank String category,
		@NotNull PropertyLocation location,
		@NotNull BigDecimal startingPrice,
		BigDecimal maxPrice,
		String priceUnit,
		String availability,
		@Size(max = 2000) String description,
		List<String> amenities,
		Map<String, Object> details,
		PropertyContact contact,
		Boolean published) {
}
