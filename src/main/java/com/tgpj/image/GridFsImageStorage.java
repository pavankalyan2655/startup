package com.tgpj.image;

import java.io.IOException;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;

@Service
public class GridFsImageStorage {

	private final GridFsTemplate gridFsTemplate;
	private final String publicBaseUrl;

	public GridFsImageStorage(
			GridFsTemplate gridFsTemplate,
			@Value("${app.public-base-url:http://localhost:8080}") String publicBaseUrl) {
		this.gridFsTemplate = gridFsTemplate;
		this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
	}

	public StoredImage upload(MultipartFile file, String propertyId, String imageId) {
		try {
			ObjectId id = gridFsTemplate.store(
					file.getInputStream(),
					file.getOriginalFilename(),
					file.getContentType(),
					Map.of("propertyId", propertyId, "imageId", imageId));
			return new StoredImage(publicBaseUrl + "/api/images/" + id, id.toHexString());
		}
		catch (IOException exception) {
			throw new IllegalStateException("Could not read uploaded image", exception);
		}
	}

	public void delete(String storageId) {
		if (!ObjectId.isValid(storageId)) {
			throw new IllegalArgumentException("Invalid stored image identifier");
		}
		gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(storageId))));
	}

	public StoredFile get(String storageId) {
		if (!ObjectId.isValid(storageId)) {
			return null;
		}
		GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(storageId))));
		if (file == null) {
			return null;
		}
		GridFsResource resource = gridFsTemplate.getResource(file);
		return new StoredFile(resource, file.getMetadata() == null ? null : file.getMetadata().getString("contentType"));
	}

	public record StoredImage(String url, String publicId) {
	}

	public record StoredFile(GridFsResource resource, String contentType) {
	}
}
