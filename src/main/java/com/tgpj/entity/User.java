package com.tgpj.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {

	@Id
	private String id;

	private String fullName;

	@Indexed(name = "email_unique_idx", unique = true)
	private String email;

	private String password;

	private UserRole role = UserRole.USER;

	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	public User() {
		this.role = UserRole.USER;
	}

	public User(String fullName, String email, String password, UserRole role) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.role = role == null ? UserRole.USER : UserRole.normalize(role.name());
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role == null ? UserRole.USER : UserRole.normalize(role.name());
	}

	public void setRole(UserRole role) {
		this.role = role == null ? UserRole.USER : UserRole.normalize(role.name());
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
