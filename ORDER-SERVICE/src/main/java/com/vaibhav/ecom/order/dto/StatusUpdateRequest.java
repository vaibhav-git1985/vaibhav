package com.vaibhav.ecom.order.dto;

import com.vaibhav.ecom.order.model.OrderStatus;

import lombok.Data;

@Data
public class StatusUpdateRequest {
	private OrderStatus status;
}
