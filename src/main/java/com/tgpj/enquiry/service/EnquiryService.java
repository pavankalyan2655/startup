package com.tgpj.enquiry.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tgpj.enquiry.EnquiryStatus;
import com.tgpj.enquiry.dto.EnquiryRequest;
import com.tgpj.enquiry.dto.EnquiryResponse;
import com.tgpj.enquiry.model.Enquiry;
import com.tgpj.enquiry.repository.EnquiryRepository;
import com.tgpj.entity.User;
import com.tgpj.property.model.Property;
import com.tgpj.property.repository.PropertyRepository;
import com.tgpj.repository.UserRepository;

@Service
public class EnquiryService {

	private final EnquiryRepository enquiryRepository;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;

	public EnquiryService(EnquiryRepository enquiryRepository, PropertyRepository propertyRepository, UserRepository userRepository) {
		this.enquiryRepository = enquiryRepository;
		this.propertyRepository = propertyRepository;
		this.userRepository = userRepository;
	}

	public EnquiryResponse submitEnquiry(EnquiryRequest request, String authenticatedUserEmail) {
		if (request == null || request.propertyId() == null || request.propertyId().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "propertyId is required");
		}
		Property property = propertyRepository.findById(request.propertyId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
		if (property.getPublicationStatus() != com.tgpj.property.PublicationStatus.PUBLISHED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Property is not available for enquiry");
		}
		User user = authenticatedUserEmail == null || authenticatedUserEmail.isBlank()
				? null
				: userRepository.findByEmail(authenticatedUserEmail).orElse(null);
		Enquiry enquiry = new Enquiry();
		enquiry.setPropertyId(property.getId());
		enquiry.setPropertyName(property.getPropertyTitle());
		enquiry.setUserId(user == null ? null : user.getId());
		enquiry.setName(request.fullName());
		enquiry.setEmail(request.email());
		enquiry.setPhone(request.phone());
		enquiry.setMessage(request.message());
		enquiry.setEnquiryStatus(EnquiryStatus.NEW);
		enquiry.setCreatedAt(Instant.now());
		enquiry.setUpdatedAt(enquiry.getCreatedAt());
		Enquiry saved = enquiryRepository.save(enquiry);
		return toResponse(saved);
	}

	public Page<EnquiryResponse> getAllEnquiries(Pageable pageable) {
		return enquiryRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
	}

	public EnquiryResponse getEnquiryById(String id) {
		return toResponse(findOrThrow(id));
	}

	public EnquiryResponse updateStatus(String id, EnquiryStatus status) {
		Enquiry enquiry = findOrThrow(id);
		enquiry.setEnquiryStatus(status);
		enquiry.setUpdatedAt(Instant.now());
		return toResponse(enquiryRepository.save(enquiry));
	}

	private Enquiry findOrThrow(String id) {
		return enquiryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enquiry not found"));
	}

	private EnquiryResponse toResponse(Enquiry enquiry) {
		return new EnquiryResponse(
				enquiry.getId(),
				enquiry.getPropertyId(),
				enquiry.getPropertyName(),
				enquiry.getUserId(),
				enquiry.getName(),
				enquiry.getEmail(),
				enquiry.getPhone(),
				enquiry.getMessage(),
				enquiry.getEnquiryStatus(),
				enquiry.getCreatedAt());
	}
}
