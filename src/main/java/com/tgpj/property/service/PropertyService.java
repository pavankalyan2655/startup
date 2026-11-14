package com.tgpj.property.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import com.tgpj.image.CloudinaryImageStorage;
import com.tgpj.image.GridFsImageStorage;
import com.tgpj.property.AvailabilityStatus;
import com.tgpj.property.PropertyType;
import com.tgpj.property.PublicationStatus;
import com.tgpj.property.dto.PropertyContact;
import com.tgpj.property.dto.PropertyImageInfo;
import com.tgpj.property.dto.PropertyLocation;
import com.tgpj.property.dto.PropertyPageResponse;
import com.tgpj.property.dto.PropertyRequest;
import com.tgpj.property.dto.PropertyResponse;
import com.tgpj.property.model.Property;
import com.tgpj.property.model.PropertyImage;
import com.tgpj.property.repository.PropertyRepository;

@Service
public class PropertyService {

	private final PropertyRepository propertyRepository;
	private final CloudinaryImageStorage imageStorage;
	private final GridFsImageStorage legacyImageStorage;

	public PropertyService(
			PropertyRepository propertyRepository,
			CloudinaryImageStorage imageStorage,
			GridFsImageStorage legacyImageStorage) {
		this.propertyRepository = propertyRepository;
		this.imageStorage = imageStorage;
		this.legacyImageStorage = legacyImageStorage;
	}

	public PropertyResponse createProperty(PropertyRequest request, String createdBy) {
		Property property = mapToEntity(request, null);
		property.setCreatedBy(createdBy);
		property.setUpdatedBy(createdBy);
		Instant now = Instant.now();
		property.setCreatedAt(now);
		property.setUpdatedAt(now);
		Property saved = propertyRepository.save(property);
		return toResponse(saved);
	}

	public PropertyPageResponse getPublicProperties(
			String search,
			String category,
			String location,
			BigDecimal minPrice,
			BigDecimal maxPrice,
			String availability,
			String sort,
			Pageable pageable) {
		Page<Property> page = propertyRepository.findAll(pageable);
		List<PropertyResponse> items = page.getContent().stream()
				.filter(property -> property != null)
				.filter(property -> property.getPublicationStatus() == PublicationStatus.PUBLISHED)
				.filter(property -> matchesSearch(property, search))
				.filter(property -> category == null || category.isBlank()
						|| property.getPropertyType() != null && property.getPropertyType().name().equalsIgnoreCase(category))
				.filter(property -> location == null || location.isBlank() || containsLocation(property, location))
				.filter(property -> minPrice == null || property.getMinimumPrice() == null || property.getMinimumPrice().compareTo(minPrice) >= 0)
				.filter(property -> maxPrice == null || property.getMaximumPrice() == null || property.getMaximumPrice().compareTo(maxPrice) <= 0)
				.filter(property -> availability == null || availability.isBlank()
						|| property.getAvailabilityStatus() != null
								&& availabilityMatches(property.getAvailabilityStatus(), availability))
				.map(this::toResponse)
				.toList();
		return new PropertyPageResponse(items, pageable.getPageNumber(), pageable.getPageSize(), items.size());
	}

	public PropertyResponse getPropertyById(String id) {
		Property property = findByIdOrThrow(id);
		if (property.getPublicationStatus() != PublicationStatus.PUBLISHED) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Property is not published");
		}
		return toResponse(property);
	}

	public PropertyResponse getAdminPropertyById(String id) {
		return toResponse(findByIdOrThrow(id));
	}

	public Page<PropertyResponse> getAllPropertiesForAdmin(
			String query,
			PropertyType propertyType,
			PublicationStatus publicationStatus,
			AvailabilityStatus availabilityStatus,
			String city,
			String locality,
			Pageable pageable) {
		Page<Property> page = propertyRepository.findAll(pageable);
		return page.map(this::toResponse);
	}

	public PropertyResponse updateProperty(String id, PropertyRequest request, String updatedBy) {
		Property property = findByIdOrThrow(id);
		Property updated = mapToEntity(request, property);
		updated.setId(property.getId());
		updated.setCreatedAt(property.getCreatedAt());
		updated.setCreatedBy(property.getCreatedBy());
		updated.setUpdatedBy(updatedBy);
		updated.setUpdatedAt(Instant.now());
		Property saved = propertyRepository.save(updated);
		return toResponse(saved);
	}

	public void deleteProperty(String id) {
		Property property = findByIdOrThrow(id);
		propertyRepository.delete(property);
	}

	public PropertyResponse publishProperty(String id, String updatedBy) {
		Property property = findByIdOrThrow(id);
		property.setPublicationStatus(PublicationStatus.PUBLISHED);
		property.setUpdatedBy(updatedBy);
		property.setUpdatedAt(Instant.now());
		return toResponse(propertyRepository.save(property));
	}

	public PropertyResponse unpublishProperty(String id, String updatedBy) {
		Property property = findByIdOrThrow(id);
		property.setPublicationStatus(PublicationStatus.UNPUBLISHED);
		property.setUpdatedBy(updatedBy);
		property.setUpdatedAt(Instant.now());
		return toResponse(propertyRepository.save(property));
	}

	public PropertyResponse updateAvailability(String id, String availabilityValue, String updatedBy) {
		Property property = findByIdOrThrow(id);
		try {
			property.setAvailabilityStatus(AvailabilityStatus.fromApiValue(availabilityValue));
		}
		catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported availability status");
		}
		property.setUpdatedBy(updatedBy);
		property.setUpdatedAt(Instant.now());
		return toResponse(propertyRepository.save(property));
	}

	public List<PropertyImageInfo> uploadImages(String propertyId, List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one image file is required");
		}
		Property property = findByIdOrThrow(propertyId);
		List<PropertyImage> images = property.getImages() == null ? new ArrayList<>() : new ArrayList<>(property.getImages());
		for (MultipartFile file : files) {
			validateImage(file);
		}

		for (MultipartFile file : files) {
			String imageId = java.util.UUID.randomUUID().toString();
			CloudinaryImageStorage.StoredImage uploaded = imageStorage.upload(file, propertyId, imageId);
			boolean cover = images.isEmpty();
			images.add(new PropertyImage(imageId, uploaded.url(), uploaded.publicId(), file.getOriginalFilename(),
					cover, images.size()));
		}

		property.setImages(images);
		property.setUpdatedAt(Instant.now());
		propertyRepository.save(property);
		return images.stream()
				.filter(image -> image != null)
				.map(image -> new PropertyImageInfo(image.getImageId(), image.getImageUrl(), "", image.isCover(),
						image.getDisplayOrder()))
				.toList();
	}

	public void deleteImage(String propertyId, String imageId) {
		Property property = findByIdOrThrow(propertyId);
		List<PropertyImage> images = property.getImages() == null ? new ArrayList<>() : new ArrayList<>(property.getImages());
		PropertyImage target = images.stream()
				.filter(image -> image != null && imageId.equals(image.getImageId()))
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property image not found"));

		if (target.getPublicId() != null && target.getPublicId().contains("/")) {
			imageStorage.delete(target.getPublicId());
		} else if (target.getPublicId() != null && !target.getPublicId().isBlank()) {
			legacyImageStorage.delete(target.getPublicId());
		}
		images.remove(target);
		if (target.isCover() && !images.isEmpty()) {
			images.get(0).setCover(true);
		}
		for (int index = 0; index < images.size(); index++) {
			images.get(index).setDisplayOrder(index);
		}
		property.setImages(images);
		property.setUpdatedAt(Instant.now());
		propertyRepository.save(property);
	}

	public List<PropertyImageInfo> reorderImages(String propertyId, List<String> imageIds) {
		Property property = findByIdOrThrow(propertyId);
		List<PropertyImage> images = property.getImages() == null ? new ArrayList<>() : new ArrayList<>(property.getImages());
		if (imageIds == null || imageIds.size() != images.size()
				|| images.stream().anyMatch(image -> image == null || !imageIds.contains(image.getImageId()))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"imageIds must contain every image ID for this property exactly once");
		}
		if (imageIds.stream().distinct().count() != imageIds.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imageIds must not contain duplicates");
		}
		Map<String, PropertyImage> imageById = new HashMap<>();
		for (PropertyImage image : images) {
			imageById.put(image.getImageId(), image);
		}
		List<PropertyImage> reordered = new ArrayList<>();
		for (int index = 0; index < imageIds.size(); index++) {
			PropertyImage image = imageById.get(imageIds.get(index));
			image.setDisplayOrder(index);
			reordered.add(image);
		}
		property.setImages(reordered);
		property.setUpdatedAt(Instant.now());
		propertyRepository.save(property);
		return toImageInfo(reordered);
	}

	public List<PropertyImageInfo> setCoverImage(String propertyId, String imageId) {
		Property property = findByIdOrThrow(propertyId);
		List<PropertyImage> images = property.getImages() == null ? new ArrayList<>() : new ArrayList<>(property.getImages());
		PropertyImage cover = images.stream()
				.filter(image -> image != null && imageId.equals(image.getImageId()))
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property image not found"));
		for (PropertyImage image : images) {
			if (image != null) {
				image.setCover(image == cover);
			}
		}
		property.setImages(images);
		property.setUpdatedAt(Instant.now());
		propertyRepository.save(property);
		return toImageInfo(images);
	}

	private List<PropertyImageInfo> toImageInfo(List<PropertyImage> images) {
		return images.stream()
				.filter(image -> image != null)
				.map(image -> new PropertyImageInfo(image.getImageId(), image.getImageUrl(),
						image.getOriginalFileName() == null ? "" : image.getOriginalFileName(),
						image.isCover(), image.getDisplayOrder()))
				.toList();
	}

	private void validateImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image files must not be empty");
		}
		if (file.getSize() > 10 * 1024 * 1024) {
			throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Each image must be 10 MB or smaller");
		}
		String contentType = file.getContentType();
		if (contentType == null || !List.of("image/jpeg", "image/png", "image/webp").contains(contentType.toLowerCase(java.util.Locale.ROOT))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG, PNG and WEBP image files are supported");
		}
	}

	private Property findByIdOrThrow(String id) {
		return propertyRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
	}

	private Property mapToEntity(PropertyRequest request, Property existing) {
		if (request == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Property request body is required");
		}
		Property property = existing != null ? existing : new Property();
		String category = request.category();
		if (category == null || category.isBlank()) {
			category = property.getPropertyType() == null ? "APARTMENT" : property.getPropertyType().name();
		}
		try {
			property.setPropertyType(PropertyType.valueOf(category.trim().toUpperCase(java.util.Locale.ROOT)));
		}
		catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Unsupported category. Use APARTMENT, VILLA, INDEPENDENT_HOUSE or PLOT.");
		}
		property.setBuilderName(request.builder());
		property.setProjectName(request.projectName());
		property.setPropertyTitle(request.title());
		property.setDescription(request.description());
		if (request.location() != null) {
			property.setCity(request.location().city());
			property.setState(request.location().state());
			property.setLocality(request.location().address());
			property.setFullAddress(request.location().address());
		}
		property.setMinimumPrice(request.startingPrice());
		property.setMaximumPrice(request.maxPrice() == null ? request.startingPrice() : request.maxPrice());
		property.setPriceUnit(request.priceUnit() == null || request.priceUnit().isBlank() ? "INR" : request.priceUnit());
		if (request.availability() != null && !request.availability().isBlank()) {
			try {
				property.setAvailabilityStatus(AvailabilityStatus.fromApiValue(request.availability()));
			}
			catch (IllegalArgumentException exception) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Unsupported availability. Use AVAILABLE, LIMITED, SOLD_OUT, NEW_LAUNCH, READY_TO_MOVE or UNDER_CONSTRUCTION.");
			}
		}
		property.setPublicationStatus(request.published() != null && request.published() ? PublicationStatus.PUBLISHED : PublicationStatus.DRAFT);
		property.setAmenities(request.amenities() == null ? Collections.emptyList() : request.amenities());
		if (request.contact() != null) {
			property.setContactPersonName(request.contact().name());
			property.setContactPhone(request.contact().phone());
			property.setContactEmail(request.contact().email());
		}
		property.setCategoryDetails(request.details() == null ? new HashMap<>() : new HashMap<>(request.details()));
		return property;
	}

	private PropertyResponse toResponse(Property property) {
		List<PropertyImageInfo> images = property.getImages() == null ? new ArrayList<>() : property.getImages().stream()
				.filter(image -> image != null)
				.map(image -> new PropertyImageInfo(image.getImageId(), image.getImageUrl(),
						image.getOriginalFileName() == null ? "" : image.getOriginalFileName(),
						image.isCover(), image.getDisplayOrder()))
				.toList();
		PropertyLocation location = new PropertyLocation(
				property.getFullAddress() == null ? property.getLocality() : property.getFullAddress(),
				property.getCity(),
				property.getState(),
				"https://maps.google.com/?q=" + encode(property.getFullAddress() == null ? property.getCity() : property.getFullAddress()));
		PropertyContact contact = new PropertyContact(
				property.getContactPersonName(),
				property.getContactPhone(),
				property.getContactEmail());
		return new PropertyResponse(
				property.getId(),
				property.getPropertyTitle(),
				property.getProjectName(),
				property.getBuilderName(),
				property.getPropertyType() == null ? "APARTMENT" : property.getPropertyType().name(),
				location,
				property.getMinimumPrice(),
				property.getMaximumPrice(),
				property.getPriceUnit(),
				property.getAvailabilityStatus() == null ? "AVAILABLE" : property.getAvailabilityStatus().name(),
				property.getDescription(),
				images,
				property.getAmenities() == null ? Collections.emptyList() : property.getAmenities(),
				property.getCategoryDetails() == null ? Collections.emptyMap() : property.getCategoryDetails(),
				contact,
				property.getPublicationStatus() == PublicationStatus.PUBLISHED);
	}

	private boolean matchesSearch(Property property, String search) {
		if (search == null || search.isBlank()) {
			return true;
		}
		String value = search.toLowerCase(java.util.Locale.ROOT);
		return (property.getBuilderName() != null && property.getBuilderName().toLowerCase(java.util.Locale.ROOT).contains(value))
				|| (property.getProjectName() != null && property.getProjectName().toLowerCase(java.util.Locale.ROOT).contains(value));
	}

	private boolean containsLocation(Property property, String location) {
		if (property == null || location == null || location.isBlank()) {
			return false;
		}
		String value = location.toLowerCase(java.util.Locale.ROOT);
		return (property.getCity() != null && property.getCity().toLowerCase(java.util.Locale.ROOT).contains(value))
				|| (property.getLocality() != null && property.getLocality().toLowerCase(java.util.Locale.ROOT).contains(value));
	}

	private boolean availabilityMatches(AvailabilityStatus status, String value) {
		try {
			return status == AvailabilityStatus.fromApiValue(value);
		}
		catch (IllegalArgumentException exception) {
			return false;
		}
	}

	private String encode(String value) {
		return java.net.URLEncoder.encode(value == null ? "" : value, java.nio.charset.StandardCharsets.UTF_8);
	}
}
