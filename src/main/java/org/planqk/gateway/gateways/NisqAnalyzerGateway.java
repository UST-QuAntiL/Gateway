package org.planqk.gateway.gateways;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

import java.net.URI;
import java.util.HashMap;

import org.planqk.gateway.dtos.AnalysisResultExecutionDto;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class NisqAnalyzerGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(NisqAnalyzerGateway.class);

    private static final String CONTEXT_PATH = "/nisq-analyzer";

    @Value("${org.planqk.gateway.nisq.analyzer.uri}")
    private String nisqAnalyzerUri;

    @Value("${server.port}")
    private String gatewayUri;

    @Value("${org.planqk.gateway.tokens.ibmq}")
    private String ibmqToken;

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
            .route("qpu-selection-result-execute", route -> route
                .path(CONTEXT_PATH + "/qpu-selection-results/*/execute")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f.filter(((exchange, chain) -> {
                        f.rewritePath("/qpu-selection-results/(?<segment>.*)/execute", "/qpu-selection-results/${segment}/execute");
                        ServerHttpRequest req = exchange.getRequest();
                        addOriginalRequestUrl(exchange, req.getURI());
                        ServerHttpRequest request = req.mutate().uri(addTokenToExecutionRequest(exchange)).build();
                        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
                        return chain.filter(exchange.mutate().request(request).build());
                    }))
                )
                .uri(nisqAnalyzerUri))
            .route("compiler-selection", route -> route
                .path(CONTEXT_PATH + "/compiler-selection")
                .and()
                .method(HttpMethod.POST)
                .filters(filter ->
                    filter.modifyRequestBody(CompilerSelectionDto.class, CompilerSelectionDto.class, this::addTokenToCompilerSelectionRequest)
                )
                .uri(nisqAnalyzerUri))
            .route("compiler-result-execute", route -> route
                .path(CONTEXT_PATH + "/compiler-results/*/execute")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f.filter(((exchange, chain) -> {
                        f.rewritePath("/compiler-results/(?<segment>.*)/execute", "/compiler-results/${segment}/execute");
                        ServerHttpRequest req = exchange.getRequest();
                        addOriginalRequestUrl(exchange, req.getURI());
                        ServerHttpRequest request = req.mutate().uri(addTokenToExecutionRequest(exchange)).build();
                        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
                        return chain.filter(exchange.mutate().request(request).build());
                    }))
                )
                .uri(nisqAnalyzerUri))
            .route("selection", route -> route
                .path(CONTEXT_PATH + "/selection")
                .and()
                .method(HttpMethod.POST)
                .filters(filter ->
                    filter.modifyRequestBody(SelectionRequestDto.class, SelectionRequestDto.class, this::addTokenToSelectionRequest)
                )
                .uri(nisqAnalyzerUri))
            .route("analysis-result-execute", route -> route
                .path(CONTEXT_PATH + "/analysis-results/*/execute")
                .and()
                .method(HttpMethod.POST)
                .filters(filter -> filter.rewritePath("/analysis-results/(?<segment>.*)/execute", "/analysis-results/${segment}/execute")
                    .modifyRequestBody(AnalysisResultExecutionDto.class, AnalysisResultExecutionDto.class, this::addTokenToAnalysisResultExecutionRequest)
                )
                .uri(nisqAnalyzerUri))
            .route("default-route", route -> route
                .path(CONTEXT_PATH + "*", CONTEXT_PATH + "/**")
                .uri(nisqAnalyzerUri)
            )
            .build();
    }

    private Publisher<SelectionRequestDto> addTokenToSelectionRequest(ServerWebExchange serverWebExchange, SelectionRequestDto selectionRequestDto) {
        if (selectionRequestDto.parameters == null) {
            selectionRequestDto.parameters = new HashMap<>();
        }
        if (!selectionRequestDto.parameters.containsKey("token") || selectionRequestDto.parameters.get("token") == null || selectionRequestDto.parameters.get("token").isBlank()) {
            selectionRequestDto.parameters.put("token", ibmqToken);
            LOGGER.debug("Added to IBMQ token to SelectionRequest");
        }

        return Mono.just(selectionRequestDto);
    }

    private Publisher<AnalysisResultExecutionDto> addTokenToAnalysisResultExecutionRequest(ServerWebExchange serverWebExchange, AnalysisResultExecutionDto analysisResultExecutionDto) {
        if (analysisResultExecutionDto.token == null || analysisResultExecutionDto.token.isBlank()) {
            analysisResultExecutionDto.token = ibmqToken;
            LOGGER.debug("Added to IBMQ token to AnalysisResultExecutionRequest");
        }

        return Mono.just(analysisResultExecutionDto);
    }

    private Publisher<CompilerSelectionDto> addTokenToCompilerSelectionRequest(ServerWebExchange serverWebExchange, CompilerSelectionDto compilerSelectionDto) {
        if (compilerSelectionDto.token == null || compilerSelectionDto.token.isBlank()) {
            compilerSelectionDto.token = ibmqToken;
            LOGGER.debug("Added to IBMQ token to CompilerSelectionRequest");
        }

        return Mono.just(compilerSelectionDto);
    }

    private Publisher<QpuSelectionDto> addTokenToQpuSelectionRequest(ServerWebExchange serverWebExchange, QpuSelectionDto selectionDto) {
        if (selectionDto.tokens == null || selectionDto.tokens.isEmpty() || selectionDto.tokens.get("IBMQ") == null || selectionDto.tokens.get("IBMQ").isBlank()) {
            selectionDto.tokens = new HashMap<>();
            selectionDto.tokens.put("ibmq", ibmqToken);
            LOGGER.debug("Added to IBMQ token to QpuSelectionRequest");
        }
        return Mono.just(selectionDto);
    }

    private URI addTokenToExecutionRequest(ServerWebExchange serverWebExchange) {
        URI newUri = serverWebExchange.getRequest().getURI();
        String tokenString = serverWebExchange.getRequest().getURI().getQuery();
        tokenString = tokenString.replace("token=", "");
        if (tokenString.isBlank() || tokenString.equals("")) {
            tokenString = "token=" + ibmqToken;
            LOGGER.debug("Added IBMQ token to ExecutionRequest");
            newUri = URI.create("http://localhost:" + gatewayUri + serverWebExchange.getRequest().getPath() + "?" + tokenString);
        }
        return newUri;
    }
}
