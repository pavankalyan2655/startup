package com.tgpj.image;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryImageStorage {

	private final RestClient restClient = RestClient.builder().build();
	private final String cloudName;
	private final String apiKey;
	private final String apiSecret;

	public CloudinaryImageStorage(
			@Value("${app.cloudinary.cloud-name:}") String cloudName,
			@Value("${app.cloudinary.api-key:}") String apiKey,
			@Value("${app.cloudinary.api-secret:}") String apiSecret) {
		this.cloudName = cloudName;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	public StoredImage upload(MultipartFile file, String propertyId, String imageId) {
		requireConfigured();
		String publicId = "properties/" + propertyId + "/" + imageId;
		String timestamp = Long.toString(Instant.now().getEpochSecond());
		Map<String, String> signedParams = Map.of("public_id", publicId, "timestamp", timestamp);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", file.getResource());
		body.add("public_id", publicId);
		body.add("timestamp", timestamp);
		body.add("api_key", apiKey);
		body.add("signature", signature(signedParams));

		Map<String, Object> response = restClient.post()
				.uri("https://api.cloudinary.com/v1_1/{cloudName}/image/upload", cloudName)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(body)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {
				});
		if (response == null || !(response.get("secure_url") instanceof String url)
				|| !(response.get("public_id") instanceof String storedPublicId)) {
			throw new IllegalStateException("Cloudinary returned an incomplete image upload response");
		}
		return new StoredImage(url, storedPublicId);
	}

	public void delete(String publicId) {
		requireConfigured();
		String timestamp = Long.toString(Instant.now().getEpochSecond());
		Map<String, String> signedParams = Map.of("public_id", publicId, "timestamp", timestamp);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("public_id", publicId);
		body.add("timestamp", timestamp);
		body.add("api_key", apiKey);
		body.add("signature", signature(signedParams));

		Map<String, Object> response = restClient.post()
				.uri("https://api.cloudinary.com/v1_1/{cloudName}/image/destroy", cloudName)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(body)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {
				});
		if (response == null || !("ok".equals(response.get("result")) || "not found".equals(response.get("result")))) {
			throw new IllegalStateException("Cloudinary failed to delete the image");
		}
	}

	private String signature(Map<String, String> parameters) {
		String canonical = new TreeMap<>(parameters).entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(java.util.stream.Collectors.joining("&"));
		try {
			byte[] digest = MessageDigest.getInstance("SHA-1")
					.digest((canonical + apiSecret).getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		}
		catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-1 is unavailable", exception);
		}
	}

	private void requireConfigured() {
		if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
			throw new IllegalStateException(
					"Set APP_CLOUDINARY_CLOUD_NAME, APP_CLOUDINARY_API_KEY and APP_CLOUDINARY_API_SECRET");
		}
	}

	public record StoredImage(String url, String publicId) {
	}
}
