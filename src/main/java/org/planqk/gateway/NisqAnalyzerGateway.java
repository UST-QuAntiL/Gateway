package org.planqk.gateway;

import java.util.HashMap;

import org.planqk.gateway.dtos.QpuSelectionDto;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class NisqAnalyzerGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(NisqAnalyzerGateway.class);

    private static final String contextPath = "/nisq-analyzer";

    @Value("${org.planqk.gateway.nisq.analyzer.uri}")
    private String nisqAnalyzerUri;

    @Value("${org.planqk.gateway.tokens.ibm}")
    private String ibmToken;

    @Bean
    public RouteLocator nisqAnalyzerLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("nisq-analyzer-content", route -> route
                .path(contextPath + "/qpu-selection")
                .and()
                .method(HttpMethod.POST)
                .filters(filter ->
                    filter.modifyRequestBody(QpuSelectionDto.class, QpuSelectionDto.class, this::addTokenToQpuSelectionRequest)
                )
                .uri(nisqAnalyzerUri)
            )
            .route("nisq-analyzer-root", route -> route
                .path(contextPath + "*")
                .uri(nisqAnalyzerUri)
            )
            .build();
    }

    private Publisher<QpuSelectionDto> addTokenToQpuSelectionRequest(ServerWebExchange serverWebExchange, QpuSelectionDto selectionDto) {
        if (selectionDto.tokens == null || selectionDto.tokens.isEmpty()) {
            selectionDto.tokens = new HashMap<>();
            selectionDto.tokens.put("ibm", ibmToken);
        }
        return Mono.just(selectionDto);
    }
}
