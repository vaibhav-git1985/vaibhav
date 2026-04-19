package com.vaibhav.ecom.delivery.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipmentResponse {
	private Long orderId;
	private String trackingNumber;
	private String status;
	private Instant createdAt;
}
