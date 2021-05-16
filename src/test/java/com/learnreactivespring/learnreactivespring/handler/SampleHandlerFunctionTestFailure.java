package com.learnreactivespring.learnreactivespring.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest
public class SampleHandlerFunctionTestFailure {
    @Autowired
    WebTestClient webTestClient;


    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get().uri("/functional/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux).expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

}
