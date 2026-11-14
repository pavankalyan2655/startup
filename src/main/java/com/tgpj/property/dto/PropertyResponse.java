package com.tgpj.property.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record PropertyResponse(
		String id,
		String title,
		String projectName,
		String builder,
		String category,
		PropertyLocation location,
		BigDecimal startingPrice,
		BigDecimal maxPrice,
		String priceUnit,
		String availability,
		String description,
		List<PropertyImageInfo> images,
		List<String> amenities,
		Map<String, Object> details,
		PropertyContact contact,
		Boolean published) {
}
