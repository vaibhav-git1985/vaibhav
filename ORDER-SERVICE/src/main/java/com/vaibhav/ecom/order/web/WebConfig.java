package com.vaibhav.ecom.order.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final UserSubInterceptor userSubInterceptor;
	private final InternalApiInterceptor internalApiInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(internalApiInterceptor)
				.addPathPatterns("/internal/**");
		registry.addInterceptor(userSubInterceptor)
				.addPathPatterns("/orders/**");
	}
}
