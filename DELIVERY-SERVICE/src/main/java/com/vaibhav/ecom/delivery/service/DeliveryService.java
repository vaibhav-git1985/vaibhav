package com.vaibhav.ecom.delivery.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import com.vaibhav.ecom.delivery.dto.ShipmentResponse;
import com.vaibhav.ecom.delivery.entity.ShipmentEntity;
import com.vaibhav.ecom.delivery.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final ShipmentRepository shipmentRepository;

	@Transactional
	public ShipmentResponse createOrGetShipment(Long orderId) {
		return shipmentRepository.findByOrderId(orderId)
				.map(this::toResponse)
				.orElseGet(() -> {
					ShipmentEntity s = new ShipmentEntity();
					s.setOrderId(orderId);
					s.setTrackingNumber("MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
					s.setStatus("SHIPPED");
					shipmentRepository.save(s);
					return toResponse(s);
				});
	}

	public ShipmentResponse getByTracking(String trackingNumber) {
		ShipmentEntity s = shipmentRepository.findByTrackingNumber(trackingNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return toResponse(s);
	}

	private ShipmentResponse toResponse(ShipmentEntity s) {
		return ShipmentResponse.builder()
				.orderId(s.getOrderId())
				.trackingNumber(s.getTrackingNumber())
				.status(s.getStatus())
				.createdAt(s.getCreatedAt())
				.build();
	}
}
