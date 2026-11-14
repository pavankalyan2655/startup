package com.tgpj.property.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tgpj.image.dto.PropertyImagesResponse;
import com.tgpj.property.AvailabilityStatus;
import com.tgpj.property.PropertyType;
import com.tgpj.property.PublicationStatus;
import com.tgpj.property.dto.PropertyRequest;
import com.tgpj.property.dto.PropertyResponse;
import com.tgpj.property.dto.ImageOrderRequest;
import com.tgpj.property.service.PropertyService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminPropertyController {

	private final PropertyService propertyService;

	public AdminPropertyController(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@PostMapping("/properties")
	@ResponseStatus(HttpStatus.CREATED)
	public PropertyResponse createProperty(
			@Valid @RequestBody PropertyRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return propertyService.createProperty(request, userDetails.getUsername());
	}

	@GetMapping("/properties")
	public Page<PropertyResponse> getAllProperties(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) PropertyType propertyType,
			@RequestParam(required = false) PublicationStatus publicationStatus,
			@RequestParam(required = false) AvailabilityStatus availabilityStatus,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String locality) {
		Pageable pageable = PageRequest.of(page, size);
		return propertyService.getAllPropertiesForAdmin(null, propertyType, publicationStatus, availabilityStatus, city,
				locality, pageable);
	}

	@GetMapping("/properties/{id}")
	public PropertyResponse getPropertyById(@PathVariable String id) {
		return propertyService.getAdminPropertyById(id);
	}

	@PutMapping("/properties/{id}")
	public PropertyResponse updateProperty(
			@PathVariable String id,
			@Valid @RequestBody PropertyRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return propertyService.updateProperty(id, request, userDetails.getUsername());
	}

	@DeleteMapping("/properties/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProperty(@PathVariable String id) {
		propertyService.deleteProperty(id);
	}

	@PatchMapping("/properties/{id}/publish")
	public PropertyResponse publishProperty(
			@PathVariable String id,
			@RequestBody(required = false) Boolean published,
			@AuthenticationPrincipal UserDetails userDetails) {
		return Boolean.FALSE.equals(published)
				? propertyService.unpublishProperty(id, userDetails.getUsername())
				: propertyService.publishProperty(id, userDetails.getUsername());
	}

	@PatchMapping("/properties/{id}/unpublish")
	public PropertyResponse unpublishProperty(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
		return propertyService.unpublishProperty(id, userDetails.getUsername());
	}

	@PatchMapping("/properties/{id}/availability")
	public PropertyResponse updateAvailability(
			@PathVariable String id,
			@RequestParam String availabilityStatus,
			@AuthenticationPrincipal UserDetails userDetails) {
		return propertyService.updateAvailability(id, availabilityStatus, userDetails.getUsername());
	}

	@PostMapping(value = "/properties/{id}/images", consumes = "multipart/form-data")
	@ResponseStatus(HttpStatus.CREATED)
	public PropertyImagesResponse uploadImages(
			@PathVariable String id,
			@RequestParam("files") List<MultipartFile> files) {
		return new PropertyImagesResponse(propertyService.uploadImages(id, files));
	}

	@DeleteMapping("/properties/{id}/images/{imageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteImage(@PathVariable String id, @PathVariable String imageId) {
		propertyService.deleteImage(id, imageId);
	}

	@PutMapping("/properties/{id}/images/order")
	public PropertyImagesResponse reorderImages(@PathVariable String id, @Valid @RequestBody ImageOrderRequest request) {
		return new PropertyImagesResponse(propertyService.reorderImages(id, request.imageIds()));
	}

	@PatchMapping("/properties/{id}/images/{imageId}/cover")
	public PropertyImagesResponse setCoverImage(@PathVariable String id, @PathVariable String imageId) {
		return new PropertyImagesResponse(propertyService.setCoverImage(id, imageId));
	}
}
