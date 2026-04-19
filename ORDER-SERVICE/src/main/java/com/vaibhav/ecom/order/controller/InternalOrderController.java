package com.vaibhav.ecom.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.ecom.order.dto.MarkPaidRequest;
import com.vaibhav.ecom.order.dto.OrderResponse;
import com.vaibhav.ecom.order.dto.StatusUpdateRequest;
import com.vaibhav.ecom.order.model.OrderStatus;
import com.vaibhav.ecom.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

	private final OrderService orderService;

	@GetMapping("/{id}")
	public OrderResponse get(@PathVariable Long id) {
		return orderService.getByIdInternal(id);
	}

	@PostMapping("/{id}/mark-paid")
	public void markPaid(@PathVariable Long id, @RequestBody(required = false) MarkPaidRequest body) {
		String key = body != null ? body.getIdempotencyKey() : null;
		orderService.markPaidInternal(id, key);
	}

	@PutMapping("/{id}/status")
	public void status(@PathVariable Long id, @RequestBody StatusUpdateRequest body) {
		orderService.updateStatusInternal(id, body.getStatus());
	}
}
