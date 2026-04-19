package com.vaibhav.ecom.order.dto;

import lombok.Data;

@Data
public class MarkPaidRequest {
	private String idempotencyKey;
}
