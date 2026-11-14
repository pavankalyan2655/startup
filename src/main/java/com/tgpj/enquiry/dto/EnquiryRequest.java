package com.tgpj.enquiry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record EnquiryRequest(
		@Size(max = 100) String propertyId,
		@Size(max = 120) String fullName,
		@Email @Size(max = 254) String email,
		@Size(max = 20) String phone,
		@Size(max = 2000) String message) {
}
