package com.tgpj.dashboard.service;

import org.springframework.stereotype.Service;

import com.tgpj.dashboard.dto.DashboardStatsResponse;
import com.tgpj.enquiry.EnquiryStatus;
import com.tgpj.enquiry.repository.EnquiryRepository;
import com.tgpj.property.AvailabilityStatus;
import com.tgpj.property.PublicationStatus;
import com.tgpj.property.repository.PropertyRepository;

@Service
public class DashboardService {

	private final PropertyRepository propertyRepository;
	private final EnquiryRepository enquiryRepository;

	public DashboardService(PropertyRepository propertyRepository, EnquiryRepository enquiryRepository) {
		this.propertyRepository = propertyRepository;
		this.enquiryRepository = enquiryRepository;
	}

	public DashboardStatsResponse getDashboardStats() {
		long totalProperties = propertyRepository.count();
		long publishedProperties = propertyRepository.countByPublicationStatus(PublicationStatus.PUBLISHED);
		long unpublishedProperties = propertyRepository.countByPublicationStatus(PublicationStatus.DRAFT) + propertyRepository.countByPublicationStatus(PublicationStatus.UNPUBLISHED);
		long availableProperties = propertyRepository.countByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
		long soldOutProperties = propertyRepository.countByAvailabilityStatus(AvailabilityStatus.SOLD_OUT);
		long totalEnquiries = enquiryRepository.count();
		long newEnquiries = enquiryRepository.countByEnquiryStatus(EnquiryStatus.NEW);
		return new DashboardStatsResponse(totalProperties, publishedProperties, unpublishedProperties,
				availableProperties, soldOutProperties, totalEnquiries, newEnquiries);
	}
}
