package com.vaibhav.ecom.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class UserSubGatewayFilter implements GlobalFilter, Ordered {

	private static final String HEADER = "X-User-Sub";

	@Value("${app.security.enabled:false}")
	private boolean securityEnabled;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (securityEnabled) {
			return ReactiveSecurityContextHolder.getContext()
					.map(ctx -> ctx.getAuthentication().getPrincipal())
					.filter(Jwt.class::isInstance)
					.map(Jwt.class::cast)
					.map(jwt -> jwt.getClaimAsString("sub"))
					.defaultIfEmpty("")
					.flatMap(sub -> forwardWithSub(exchange, chain, sub));
		}
		String existing = exchange.getRequest().getHeaders().getFirst(HEADER);
		return forwardWithSub(exchange, chain, existing != null ? existing : "");
	}

	private Mono<Void> forwardWithSub(ServerWebExchange exchange, GatewayFilterChain chain, String sub) {
		if (sub == null || sub.isBlank()) {
			return chain.filter(exchange);
		}
		ServerHttpRequest request = exchange.getRequest().mutate().header(HEADER, sub).build();
		return chain.filter(exchange.mutate().request(request).build());
	}

	@Override
	public int getOrder() {
		/* Run after security has populated the reactive security context */
		return Ordered.LOWEST_PRECEDENCE - 10;
	}
}
