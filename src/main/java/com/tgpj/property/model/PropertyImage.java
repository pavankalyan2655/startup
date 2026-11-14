package com.tgpj.property.model;

import java.time.Instant;

public class PropertyImage {

	private String imageId;
	private String imageUrl;
	private String publicId;
	private String originalFileName;
	private boolean isCover;
	private int displayOrder;
	private Instant uploadedAt;

	public PropertyImage() {
		this.uploadedAt = Instant.now();
	}

	public PropertyImage(String imageId, String imageUrl, String publicId, String originalFileName, boolean isCover,
			int displayOrder) {
		this.imageId = imageId;
		this.imageUrl = imageUrl;
		this.publicId = publicId;
		this.originalFileName = originalFileName;
		this.isCover = isCover;
		this.displayOrder = displayOrder;
		this.uploadedAt = Instant.now();
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public boolean isCover() {
		return isCover;
	}

	public void setCover(boolean cover) {
		isCover = cover;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Instant getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(Instant uploadedAt) {
		this.uploadedAt = uploadedAt;
	}
}
