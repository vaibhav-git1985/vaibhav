package com.vaibhav.ecom.delivery.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InternalApiInterceptor implements HandlerInterceptor {

	@Value("${app.internal-api-key}")
	private String internalApiKey;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String key = request.getHeader("X-Internal-Api-Key");
		if (key == null || !key.equals(internalApiKey)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid internal API key");
		}
		return true;
	}
}
