package com.vaibhav.ecom.payment.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.vaibhav.ecom.payment.dto.OrderResponse;

@Component
public class OrderServiceClient {

	private final RestTemplate restTemplate;

	private final String internalApiKey;

	public OrderServiceClient(@Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate,
			@Value("${app.internal-api-key}") String internalApiKey) {
		this.restTemplate = restTemplate;
		this.internalApiKey = internalApiKey;
	}

	public OrderResponse getOrderInternal(Long orderId) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Internal-Api-Key", internalApiKey);
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<OrderResponse> res = restTemplate.exchange(
				"http://ORDER-SERVICE/internal/orders/{id}",
				HttpMethod.GET,
				entity,
				OrderResponse.class,
				orderId);
		return res.getBody();
	}

	public void markPaid(Long orderId, String idempotencyKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Internal-Api-Key", internalApiKey);
		headers.set("Content-Type", "application/json");
		String body = idempotencyKey != null
				? "{\"idempotencyKey\":\"" + idempotencyKey.replace("\"", "") + "\"}"
				: "{}";
		HttpEntity<String> entity = new HttpEntity<>(body, headers);
		restTemplate.postForEntity(
				"http://ORDER-SERVICE/internal/orders/{id}/mark-paid",
				entity,
				Void.class,
				orderId);
	}
}
