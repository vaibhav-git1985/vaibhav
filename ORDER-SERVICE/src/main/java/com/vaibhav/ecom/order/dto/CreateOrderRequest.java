package com.vaibhav.ecom.order.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRequest {
	private List<Line> items;

	@Data
	public static class Line {
		private String productId;
		private String productName;
		private int quantity;
		private BigDecimal unitPrice;
	}
}
