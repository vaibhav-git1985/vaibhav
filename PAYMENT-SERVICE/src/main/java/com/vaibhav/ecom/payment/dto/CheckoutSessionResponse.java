package com.vaibhav.ecom.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutSessionResponse {
	private String checkoutUrl;
	private String sessionId;
}
