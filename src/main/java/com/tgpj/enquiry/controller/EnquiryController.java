package com.tgpj.enquiry.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tgpj.enquiry.EnquiryStatus;
import com.tgpj.enquiry.dto.EnquiryRequest;
import com.tgpj.enquiry.dto.EnquiryResponse;
import com.tgpj.enquiry.service.EnquiryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class EnquiryController {

	private final EnquiryService enquiryService;

	public EnquiryController(EnquiryService enquiryService) {
		this.enquiryService = enquiryService;
	}

	@PostMapping("/enquiries")
	@ResponseStatus(HttpStatus.CREATED)
	public EnquiryResponse submitEnquiry(@Valid @RequestBody EnquiryRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (request == null || request.propertyId() == null || request.propertyId().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "propertyId is required");
		}
		String authEmail = userDetails == null ? null : userDetails.getUsername();
		return enquiryService.submitEnquiry(request, authEmail);
	}

	@GetMapping("/admin/enquiries")
	@PreAuthorize("hasRole('ADMIN')")
	public Page<EnquiryResponse> getAllEnquiries(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return enquiryService.getAllEnquiries(pageable);
	}

	@GetMapping("/admin/enquiries/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public EnquiryResponse getEnquiryById(@PathVariable String id) {
		return enquiryService.getEnquiryById(id);
	}

	@PatchMapping("/admin/enquiries/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public EnquiryResponse updateStatus(@PathVariable String id, @RequestParam EnquiryStatus status) {
		return enquiryService.updateStatus(id, status);
	}
}
