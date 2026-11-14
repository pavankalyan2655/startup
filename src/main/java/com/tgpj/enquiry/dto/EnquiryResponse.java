package com.tgpj.enquiry.dto;

import java.time.Instant;

import com.tgpj.enquiry.EnquiryStatus;

public record EnquiryResponse(
		String id,
		String propertyId,
		String propertyName,
		String userId,
		String fullName,
		String email,
		String phone,
		String message,
		EnquiryStatus enquiryStatus,
		Instant createdAt) {
}
