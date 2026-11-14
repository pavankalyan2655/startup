package com.tgpj.property.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.tgpj.property.AvailabilityStatus;
import com.tgpj.property.PropertyType;
import com.tgpj.property.PublicationStatus;
import com.tgpj.property.model.Property;

public interface PropertyRepository extends MongoRepository<Property, String> {

	Page<Property> findByPublicationStatus(PublicationStatus publicationStatus, Pageable pageable);

	Page<Property> findByPropertyTypeAndPublicationStatus(PropertyType propertyType, PublicationStatus publicationStatus,
			Pageable pageable);

	List<Property> findByCityAndPublicationStatus(String city, PublicationStatus publicationStatus);

	Page<Property> findByAvailabilityStatusAndPublicationStatus(AvailabilityStatus availabilityStatus,
			PublicationStatus publicationStatus, Pageable pageable);

	long countByPublicationStatus(PublicationStatus publicationStatus);

	long countByAvailabilityStatus(AvailabilityStatus availabilityStatus);
}
