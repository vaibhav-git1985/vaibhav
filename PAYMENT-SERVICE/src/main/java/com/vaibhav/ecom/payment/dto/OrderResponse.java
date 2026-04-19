package com.vaibhav.ecom.payment.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {
	private Long id;
	private String userSub;
	private String status;
	private BigDecimal totalAmount;
	private List<Object> lineItems;
}
