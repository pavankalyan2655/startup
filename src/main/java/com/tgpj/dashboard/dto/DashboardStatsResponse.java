package com.tgpj.dashboard.dto;

public record DashboardStatsResponse(
		long totalProperties,
		long publishedProperties,
		long unpublishedProperties,
		long availableProperties,
		long soldOutProperties,
		long totalEnquiries,
		long newEnquiries) {
}
