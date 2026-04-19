package com.vaibhav.ecom.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.ecom.order.dto.CreateOrderRequest;
import com.vaibhav.ecom.order.dto.OrderResponse;
import com.vaibhav.ecom.order.service.OrderService;
import com.vaibhav.ecom.order.web.UserSubInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public OrderResponse create(HttpServletRequest request, @RequestBody CreateOrderRequest body) {
		String sub = (String) request.getAttribute(UserSubInterceptor.ATTR_USER_SUB);
		return orderService.createOrder(sub, body);
	}

	@GetMapping
	public List<OrderResponse> list(HttpServletRequest request) {
		String sub = (String) request.getAttribute(UserSubInterceptor.ATTR_USER_SUB);
		return orderService.listForUser(sub);
	}

	@GetMapping("/{id}")
	public OrderResponse get(HttpServletRequest request, @PathVariable Long id) {
		String sub = (String) request.getAttribute(UserSubInterceptor.ATTR_USER_SUB);
		return orderService.getForUser(id, sub);
	}
}
