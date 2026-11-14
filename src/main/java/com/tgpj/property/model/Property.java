package com.tgpj.property.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tgpj.property.AvailabilityStatus;
import com.tgpj.property.PropertyType;
import com.tgpj.property.PublicationStatus;

@Document(collection = "properties")
public class Property {

	@Id
	private String id;

	@Indexed
	private PropertyType propertyType;

	private String builderName;
	private String projectName;
	private String propertyTitle;
	private String description;
	private String city;
	private String state;
	private String locality;
	private String fullAddress;
	private Double latitude;
	private Double longitude;

	private BigDecimal minimumPrice;
	private BigDecimal maximumPrice;
	private String priceUnit = "INR";

	@Indexed
	private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

	@Indexed
	private PublicationStatus publicationStatus = PublicationStatus.DRAFT;

	private List<String> amenities = new ArrayList<>();
	private List<PropertyImage> images = new ArrayList<>();
	private String contactPersonName;
	private String contactPhone;
	private String contactEmail;

	private Map<String, Object> categoryDetails = new HashMap<>();

	private String createdBy;
	private String updatedBy;

	private Instant createdAt;
	private Instant updatedAt;

	public Property() {
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
		this.availabilityStatus = AvailabilityStatus.AVAILABLE;
		this.publicationStatus = PublicationStatus.DRAFT;
		this.priceUnit = "INR";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public String getBuilderName() {
		return builderName;
	}

	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getPropertyTitle() {
		return propertyTitle;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setPropertyTitle(String propertyTitle) {
		this.propertyTitle = propertyTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getMinimumPrice() {
		return minimumPrice;
	}

	public void setMinimumPrice(BigDecimal minimumPrice) {
		this.minimumPrice = minimumPrice;
	}

	public BigDecimal getMaximumPrice() {
		return maximumPrice;
	}

	public void setMaximumPrice(BigDecimal maximumPrice) {
		this.maximumPrice = maximumPrice;
	}

	public String getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(String priceUnit) {
		this.priceUnit = priceUnit;
	}

	public AvailabilityStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

	public PublicationStatus getPublicationStatus() {
		return publicationStatus;
	}

	public void setPublicationStatus(PublicationStatus publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	public List<String> getAmenities() {
		return amenities;
	}

	public void setAmenities(List<String> amenities) {
		this.amenities = amenities == null ? new ArrayList<>() : amenities;
	}

	public List<PropertyImage> getImages() {
		return images;
	}

	public void setImages(List<PropertyImage> images) {
		this.images = images == null ? new ArrayList<>() : images;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public Map<String, Object> getCategoryDetails() {
		return categoryDetails;
	}

	public void setCategoryDetails(Map<String, Object> categoryDetails) {
		this.categoryDetails = categoryDetails == null ? new HashMap<>() : categoryDetails;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
