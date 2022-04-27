package org.planqk.gateway;

import org.junit.jupiter.api.Test;
import org.planqk.gateway.dtos.QpuSelectionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "org.planqk.gateway.nisq.analyzer.uri=http://localhost:${wiremock.server.port}",
        "org.planqk.gateway.tokens.ibm=myTestToken"
    }
)
@AutoConfigureWireMock(port = 0)
class GatewayApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testGateway() {
        stubFor(
            get(urlEqualTo("/nisq-analyzer"))
                .willReturn(aResponse()
                    .withBody("{\"result\": \"success\"}")
                    .withHeader("Content-Type", "application/json"))
        );

        webClient.get()
            .uri("/nisq-analyzer")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.result").isEqualTo("success");
    }

    @Test
    void testQpuSelectionIsEnrichedWithToken() {
        stubFor(
            post(urlEqualTo("/nisq-analyzer/qpu-selection"))
                .withRequestBody(matchingJsonPath("$.tokens.ibm", containing("myTestToken")))
                .willReturn(aResponse())
        );

        webClient.post()
            .uri("/nisq-analyzer/qpu-selection")
            .body(Mono.just(new QpuSelectionDto()), QpuSelectionDto.class)
            .exchange()
            .expectStatus().isOk();
    }
}
