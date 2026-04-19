package com.vaibhav.ecom.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
			@Value("${app.security.enabled:false}") boolean securityEnabled,
			@Value("${OKTA_ISSUER_URI:}") String issuerUri) {
		http.csrf(ServerHttpSecurity.CsrfSpec::disable);
		if (!securityEnabled) {
			return http.authorizeExchange(ex -> ex.anyExchange().permitAll()).build();
		}
		if (issuerUri == null || issuerUri.isBlank()) {
			throw new IllegalStateException("OKTA_ISSUER_URI must be set when app.security.enabled=true");
		}
		http.authorizeExchange(ex -> ex
				.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.pathMatchers(HttpMethod.GET, "/products", "/products/**").permitAll()
				.pathMatchers(HttpMethod.GET, "/delivery/track/**").permitAll()
				.anyExchange().authenticated()
		).oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.jwtDecoder(ReactiveJwtDecoders.fromIssuerLocation(issuerUri))));
		return http.build();
	}
}
