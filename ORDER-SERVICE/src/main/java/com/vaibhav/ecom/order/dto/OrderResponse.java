package com.vaibhav.ecom.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.vaibhav.ecom.order.model.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
	private Long id;
	private String userSub;
	private OrderStatus status;
	private BigDecimal totalAmount;
	private Instant createdAt;
	private List<LineItemResponse> lineItems;

	@Data
	@Builder
	public static class LineItemResponse {
		private String productId;
		private String productName;
		private int quantity;
		private BigDecimal unitPrice;
	}
}
