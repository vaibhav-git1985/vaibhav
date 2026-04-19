package com.vaibhav.ecom.payment.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WorkerClient {

	private final RestTemplate externalRestTemplate;

	private final String internalApiKey;

	private final String workerEnqueueUrl;

	public WorkerClient(@Qualifier("externalRestTemplate") RestTemplate externalRestTemplate,
			@Value("${app.internal-api-key}") String internalApiKey,
			@Value("${app.worker-enqueue-url}") String workerEnqueueUrl) {
		this.externalRestTemplate = externalRestTemplate;
		this.internalApiKey = internalApiKey;
		this.workerEnqueueUrl = workerEnqueueUrl;
	}

	public void enqueueDeliver(Long orderId, String idempotencyKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Internal-Api-Key", internalApiKey);
		headers.set("Content-Type", "application/json");
		String safeKey = idempotencyKey == null ? "" : idempotencyKey.replace("\"", "\\\"");
		String body = "{\"orderId\":" + orderId + ",\"idempotencyKey\":\"" + safeKey + "\"}";
		externalRestTemplate.postForEntity(workerEnqueueUrl, new HttpEntity<>(body, headers), Void.class);
	}
}
