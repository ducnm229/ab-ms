package com.ab.ms.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
@Order(1)
public class CorrelationIdFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);

    private static final String CORRELATION_ID_HEADER = "abBank-correlation-id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String correlationId = getOrCreateCorrelationId(exchange);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.header(CORRELATION_ID_HEADER, correlationId))
                .build();

        return chain.filter(mutatedExchange)
                .then(Mono.fromRunnable(() -> {
                    mutatedExchange.getResponse()
                            .getHeaders()
                            .add(CORRELATION_ID_HEADER, correlationId);

                    logger.debug("Correlation ID added to response: {}", correlationId);
                }));
    }

    private String getOrCreateCorrelationId(ServerWebExchange exchange) {
        return Optional.ofNullable(
                        exchange.getRequest()
                                .getHeaders()
                                .getFirst(CORRELATION_ID_HEADER))
                .orElseGet(() -> {
                    String id = UUID.randomUUID().toString();
                    logger.debug("Generated correlation ID: {}", id);
                    return id;
                });
    }
}

