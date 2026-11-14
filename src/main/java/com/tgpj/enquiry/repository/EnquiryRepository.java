package com.tgpj.enquiry.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.tgpj.enquiry.EnquiryStatus;
import com.tgpj.enquiry.model.Enquiry;

public interface EnquiryRepository extends MongoRepository<Enquiry, String> {

	List<Enquiry> findByPropertyId(String propertyId);

	Page<Enquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);

	long countByEnquiryStatus(EnquiryStatus enquiryStatus);
}
