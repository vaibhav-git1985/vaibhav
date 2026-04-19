package com.vaibhav.ecom.delivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaibhav.ecom.delivery.entity.ShipmentEntity;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, Long> {

	Optional<ShipmentEntity> findByOrderId(Long orderId);

	Optional<ShipmentEntity> findByTrackingNumber(String trackingNumber);
}
