package org.planqk.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class NisqAnalyzerGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(NisqAnalyzerGateway.class);

    @Value("${org.planqk.gateway.nisq.analyzer.uri}")
    private String nisqAnalyzerUri;

    @Bean
    public RouteLocator nisqAnalyzerLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("nisq-analyzer-root", route -> route.path("/nisq-analyzer*")
                .filters(f -> f.modifyRequestBody(String.class, String.class, MediaType.APPLICATION_JSON_VALUE, (serverWebExchange, s) -> {
                    LOGGER.debug("Received Request!");
                    return Mono.just(s != null ? s : "");
                }))
                .uri(nisqAnalyzerUri)
            )
            .build();
    }
}
