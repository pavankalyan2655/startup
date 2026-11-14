package com.tgpj.property.dto;

import java.util.List;

public record PropertyPageResponse(
		List<PropertyResponse> items,
		int page,
		int pageSize,
		long total) {
}
