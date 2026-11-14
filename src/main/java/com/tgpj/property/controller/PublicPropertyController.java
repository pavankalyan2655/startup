package com.tgpj.property.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tgpj.property.PropertyType;
import com.tgpj.property.dto.PropertyPageResponse;
import com.tgpj.property.dto.PropertyResponse;
import com.tgpj.property.service.PropertyService;

@RestController
@RequestMapping("/api")
public class PublicPropertyController {

	private final PropertyService propertyService;

	public PublicPropertyController(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@GetMapping("/properties")
	public PropertyPageResponse getPublicProperties(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(required = false) String availability,
			@RequestParam(defaultValue = "featured") String sort,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int pageSize) {
		Pageable pageable = PageRequest.of(page, pageSize);
		return propertyService.getPublicProperties(search, category, location, minPrice, maxPrice, availability, sort, pageable);
	}

	@GetMapping("/properties/{id}")
	public PropertyResponse getPropertyById(@PathVariable String id) {
		return propertyService.getPropertyById(id);
	}

	@GetMapping("/properties/type/{propertyType}")
	public List<PropertyResponse> getPropertiesByType(@PathVariable String propertyType) {
		return propertyService.getPublicProperties(null, propertyType, null, null, null, null, "featured",
				PageRequest.of(0, 50)).items();
	}

	@GetMapping("/property-categories")
	public List<String> propertyCategories() {
		return Arrays.stream(PropertyType.values()).map(Enum::name).toList();
	}

	@GetMapping("/locations")
	public List<String> locations() {
		return List.of("Hyderabad", "Bengaluru", "Chennai", "Mumbai", "Pune");
	}

	@GetMapping("/builders")
	public List<String> builders() {
		return List.of("My Home", "Rajapushpa", "Bhuga");
	}
}
