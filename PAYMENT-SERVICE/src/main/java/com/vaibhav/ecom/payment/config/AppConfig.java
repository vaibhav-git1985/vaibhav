package com.vaibhav.ecom.payment.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	@Bean
	@LoadBalanced
	RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	RestTemplate externalRestTemplate() {
		return new RestTemplate();
	}
}
