package com.tgpj.image;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageController {

	private final GridFsImageStorage imageStorage;

	public ImageController(GridFsImageStorage imageStorage) {
		this.imageStorage = imageStorage;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Resource> getImage(@PathVariable String id) throws IOException {
		GridFsImageStorage.StoredFile storedFile = imageStorage.get(id);
		if (storedFile == null) {
			return ResponseEntity.notFound().build();
		}

		MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
		if (storedFile.contentType() != null) {
			try {
				mediaType = MediaType.parseMediaType(storedFile.contentType());
			}
			catch (IllegalArgumentException ignored) {
				mediaType = MediaType.APPLICATION_OCTET_STREAM;
			}
		}
		return ResponseEntity.ok()
				.contentType(mediaType)
				.cacheControl(CacheControl.noCache())
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline")
				.body(storedFile.resource());
	}
}
