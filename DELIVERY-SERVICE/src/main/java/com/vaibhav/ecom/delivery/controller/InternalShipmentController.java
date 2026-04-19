package com.vaibhav.ecom.delivery.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.ecom.delivery.dto.CreateShipmentRequest;
import com.vaibhav.ecom.delivery.dto.ShipmentResponse;
import com.vaibhav.ecom.delivery.service.DeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/shipments")
@RequiredArgsConstructor
public class InternalShipmentController {

	private final DeliveryService deliveryService;

	@PostMapping
	public ShipmentResponse create(@RequestBody CreateShipmentRequest body) {
		return deliveryService.createOrGetShipment(body.getOrderId());
	}
}
