package com.tgpj.enquiry.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tgpj.enquiry.EnquiryStatus;

@Document(collection = "enquiries")
public class Enquiry {

	@Id
	private String id;

	@Indexed
	private String propertyId;
	private String propertyName;
	private String userId;
	private String name;
	private String email;
	private String phone;
	private String message;

	@Indexed
	private EnquiryStatus enquiryStatus = EnquiryStatus.NEW;

	private Instant createdAt;
	private Instant updatedAt;

	public Enquiry() {
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EnquiryStatus getEnquiryStatus() {
		return enquiryStatus;
	}

	public void setEnquiryStatus(EnquiryStatus enquiryStatus) {
		this.enquiryStatus = enquiryStatus;
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
