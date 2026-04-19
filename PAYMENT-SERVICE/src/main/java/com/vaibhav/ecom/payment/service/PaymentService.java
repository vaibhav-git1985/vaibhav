package com.vaibhav.ecom.payment.service;

import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vaibhav.ecom.payment.client.OrderServiceClient;
import com.vaibhav.ecom.payment.client.WorkerClient;
import com.vaibhav.ecom.payment.dto.CheckoutSessionResponse;
import com.vaibhav.ecom.payment.dto.OrderResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	@Value("${stripe.secret-key:}")
	private String stripeSecretKey;

	@Value("${app.base-url}")
	private String appBaseUrl;

	private final OrderServiceClient orderServiceClient;
	private final WorkerClient workerClient;

	@PostConstruct
	void initStripe() {
		if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
			Stripe.apiKey = stripeSecretKey;
		}
	}

	public CheckoutSessionResponse createCheckoutSession(String userSub, Long orderId) {
		requireStripe();
		OrderResponse order = orderServiceClient.getOrderInternal(orderId);
		if (order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
		}
		if (!Objects.equals(order.getUserSub(), userSub)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order does not belong to user");
		}
		if (!"CREATED".equalsIgnoreCase(order.getStatus())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is not payable in current state");
		}
		if (order.getTotalAmount() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order total missing");
		}
		long amountCents = order.getTotalAmount().setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
		if (amountCents <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order amount");
		}
		try {
			SessionCreateParams params = SessionCreateParams.builder()
					.setMode(SessionCreateParams.Mode.PAYMENT)
					.setSuccessUrl(appBaseUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
					.setCancelUrl(appBaseUrl + "/checkout/cancel")
					.putMetadata("orderId", String.valueOf(orderId))
					.putMetadata("userSub", userSub)
					.addLineItem(
							SessionCreateParams.LineItem.builder()
									.setQuantity(1L)
									.setPriceData(
											SessionCreateParams.LineItem.PriceData.builder()
													.setCurrency("usd")
													.setUnitAmount(amountCents)
													.setProductData(
															SessionCreateParams.LineItem.PriceData.ProductData.builder()
																	.setName("Order #" + orderId)
																	.build())
													.build())
									.build())
					.build();
			Session session = Session.create(params);
			return new CheckoutSessionResponse(session.getUrl(), session.getId());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Stripe error: " + e.getMessage());
		}
	}

	public void verifyCheckoutSession(String userSub, String sessionId) {
		requireStripe();
		try {
			Session session = Session.retrieve(sessionId);
			Map<String, String> meta = session.getMetadata();
			if (meta == null || !userSub.equals(meta.get("userSub"))) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Session does not belong to user");
			}
			if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment not completed");
			}
			Long orderId = Long.parseLong(meta.get("orderId"));
			orderServiceClient.markPaid(orderId, sessionId);
			workerClient.enqueueDeliver(orderId, sessionId);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Stripe verify error: " + e.getMessage());
		}
	}

	private void requireStripe() {
		if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Stripe is not configured (set STRIPE_SECRET_KEY)");
		}
	}
}
