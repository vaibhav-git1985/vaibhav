package com.vaibhav.ecom.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.ecom.payment.dto.CheckoutSessionRequest;
import com.vaibhav.ecom.payment.dto.CheckoutSessionResponse;
import com.vaibhav.ecom.payment.dto.VerifySessionRequest;
import com.vaibhav.ecom.payment.service.PaymentService;
import com.vaibhav.ecom.payment.web.UserSubInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/checkout-session")
	public CheckoutSessionResponse checkout(HttpServletRequest request, @RequestBody CheckoutSessionRequest body) {
		String sub = (String) request.getAttribute(UserSubInterceptor.ATTR_USER_SUB);
		return paymentService.createCheckoutSession(sub, body.getOrderId());
	}

	@PostMapping("/verify-session")
	public void verify(HttpServletRequest request, @RequestBody VerifySessionRequest body) {
		String sub = (String) request.getAttribute(UserSubInterceptor.ATTR_USER_SUB);
		paymentService.verifyCheckoutSession(sub, body.getSessionId());
	}
}
