package com.vaibhav.ecom.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vaibhav.ecom.order.dto.CreateOrderRequest;
import com.vaibhav.ecom.order.dto.OrderResponse;
import com.vaibhav.ecom.order.entity.OrderEntity;
import com.vaibhav.ecom.order.entity.OrderLineItemEntity;
import com.vaibhav.ecom.order.model.OrderStatus;
import com.vaibhav.ecom.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;

	@Transactional
	public OrderResponse createOrder(String userSub, CreateOrderRequest request) {
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items required");
		}
		OrderEntity order = new OrderEntity();
		order.setUserSub(userSub);
		order.setStatus(OrderStatus.CREATED);
		BigDecimal total = BigDecimal.ZERO;
		for (CreateOrderRequest.Line line : request.getItems()) {
			if (line.getQuantity() <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid quantity");
			}
			OrderLineItemEntity li = new OrderLineItemEntity();
			li.setOrder(order);
			li.setProductId(line.getProductId());
			li.setProductName(line.getProductName());
			li.setQuantity(line.getQuantity());
			li.setUnitPrice(line.getUnitPrice() != null ? line.getUnitPrice() : BigDecimal.ZERO);
			order.getLineItems().add(li);
			total = total.add(li.getUnitPrice().multiply(BigDecimal.valueOf(li.getQuantity())));
		}
		order.setTotalAmount(total);
		orderRepository.save(order);
		return toResponse(order);
	}

	public List<OrderResponse> listForUser(String userSub) {
		return orderRepository.findByUserSubOrderByCreatedAtDesc(userSub).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	public OrderResponse getForUser(Long id, String userSub) {
		OrderEntity order = orderRepository.findByIdAndUserSub(id, userSub)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return toResponse(order);
	}

	public OrderResponse getByIdInternal(Long id) {
		OrderEntity order = orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return toResponse(order);
	}

	@Transactional
	public void markPaidInternal(Long id, String idempotencyKey) {
		OrderEntity order = orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.FULFILLMENT_QUEUED
				|| order.getStatus() == OrderStatus.SHIPPED) {
			return;
		}
		if (order.getStatus() != OrderStatus.CREATED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "order not payable");
		}
		order.setStatus(OrderStatus.PAID);
		orderRepository.save(order);
	}

	@Transactional
	public void updateStatusInternal(Long id, OrderStatus status) {
		OrderEntity order = orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		order.setStatus(status);
		orderRepository.save(order);
	}

	private OrderResponse toResponse(OrderEntity order) {
		return OrderResponse.builder()
				.id(order.getId())
				.userSub(order.getUserSub())
				.status(order.getStatus())
				.totalAmount(order.getTotalAmount())
				.createdAt(order.getCreatedAt())
				.lineItems(order.getLineItems().stream()
						.map(li -> OrderResponse.LineItemResponse.builder()
								.productId(li.getProductId())
								.productName(li.getProductName())
								.quantity(li.getQuantity())
								.unitPrice(li.getUnitPrice())
								.build())
						.collect(Collectors.toList()))
				.build();
	}
}
