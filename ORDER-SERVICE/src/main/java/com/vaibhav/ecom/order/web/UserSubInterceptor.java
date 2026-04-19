package com.vaibhav.ecom.order.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserSubInterceptor implements HandlerInterceptor {

	public static final String ATTR_USER_SUB = "X_USER_SUB";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String sub = request.getHeader("X-User-Sub");
		if (sub == null || sub.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-User-Sub");
		}
		request.setAttribute(ATTR_USER_SUB, sub);
		return true;
	}
}
