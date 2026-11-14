package com.tgpj.property.dto;

public record PropertyImageInfo(
		String id,
		String url,
		String altText,
		boolean isPrimary,
		int displayOrder) {
}
