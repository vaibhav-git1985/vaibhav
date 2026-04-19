package com.vaibhav.ecom.delivery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.ecom.delivery.dto.ShipmentResponse;
import com.vaibhav.ecom.delivery.service.DeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

	private final DeliveryService deliveryService;

	@GetMapping("/track/{trackingNumber}")
	public ShipmentResponse track(@PathVariable String trackingNumber) {
		return deliveryService.getByTracking(trackingNumber);
	}
}
