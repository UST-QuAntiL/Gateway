package org.planqk.gateway.gateways;

import java.util.HashMap;

import org.planqk.gateway.dtos.CompilerSelectionDto;
import org.planqk.gateway.dtos.QpuSelectionDto;
import org.planqk.gateway.dtos.SelectionRequestDto;
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

    private static final String CONTEXT_PATH = "/nisq-analyzer";

    @Value("${org.planqk.gateway.nisq.analyzer.uri}")
    private String nisqAnalyzerUri;

    @Value("${org.planqk.gateway.tokens.ibm}")
    private String ibmToken;

    @Bean
    public RouteLocator nisqAnalyzerLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("qpu-selection", route -> route
                .path(CONTEXT_PATH + "/qpu-selection")
                .and()
                .method(HttpMethod.POST)
                .filters(filter ->
                    filter.modifyRequestBody(QpuSelectionDto.class, QpuSelectionDto.class, this::addTokenToQpuSelectionRequest)
                )
                .uri(nisqAnalyzerUri)
            )
            .route("compiler-selection", route -> route
                .path(CONTEXT_PATH + "/compiler-selection")
                .and()
                .method(HttpMethod.POST)
                .filters(filter ->
                    filter.modifyRequestBody(CompilerSelectionDto.class, CompilerSelectionDto.class, this::addTokenToCompilerSelectionRequest)
                )
                .uri(nisqAnalyzerUri))
            .route("compiler-selection", route -> route
                .path(CONTEXT_PATH + "/selection")
                .and()
                .method(HttpMethod.POST)
                .filters(filter ->
                    filter.modifyRequestBody(SelectionRequestDto.class, SelectionRequestDto.class, this::addTokenToSelectionRequest)
                )
                .uri(nisqAnalyzerUri))
            .route("default-route", route -> route
                .path(CONTEXT_PATH + "*")
                .uri(nisqAnalyzerUri)
            )
            .build();
    }

    private Publisher<SelectionRequestDto> addTokenToSelectionRequest(ServerWebExchange serverWebExchange, SelectionRequestDto selectionRequestDto) {
        if (selectionRequestDto.parameters == null) {
            selectionRequestDto.parameters = new HashMap<>();
        }
        if (!selectionRequestDto.parameters.containsKey("token")) {
            selectionRequestDto.parameters.put("token", ibmToken);
        }

        return Mono.just(selectionRequestDto);
    }

    private Publisher<CompilerSelectionDto> addTokenToCompilerSelectionRequest(ServerWebExchange serverWebExchange, CompilerSelectionDto compilerSelectionDto) {
        if (compilerSelectionDto.token == null || compilerSelectionDto.token.isBlank()) {
            compilerSelectionDto.token = ibmToken;
        }

        return Mono.just(compilerSelectionDto);
    }

    private Publisher<QpuSelectionDto> addTokenToQpuSelectionRequest(ServerWebExchange serverWebExchange, QpuSelectionDto selectionDto) {
        if (selectionDto.tokens == null || selectionDto.tokens.isEmpty()) {
            selectionDto.tokens = new HashMap<>();
            selectionDto.tokens.put("ibm", ibmToken);
        }
        return Mono.just(selectionDto);
    }
}
