package com.learnreactivespring.learnreactivespring.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

//@@@3
@WebFluxTest    //@WebFluxTest scans the @Controller and @RestController annotations and not the @Component @Repository @Service
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;    //non blocking client -> WebFluxTest creates an instance of webTestClient

    @Test
    public void flux_approach1() {
        //Basic testing of APIs using the StepVerifier

        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")    //prepare a get() with url /flux
                .accept(MediaType.APPLICATION_NDJSON)   //and accept as MediaType.APPLICATION_NDJSON
                .exchange() //call the API with exchange()
                .expectStatus().isOk()  //expect the status as OK
                .returnResult(Integer.class)    //return the FLUX as Integer
                .getResponseBody(); //Return the response Body

        StepVerifier.create(integerFlux)
                .expectSubscription()   //create the flux and expect subscription
                .expectNext(1, 2, 3, 4) //expect 1,2,3,4 to be emitted from the flux
                .verifyComplete()
                ;  //verify if what we are expecting is equal to the actual value.
    }


    @Test
    public void flux_approach2() {
        //Approach to check the elements using the size of numbers of elements emitted.

        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_NDJSON)
                .expectBodyList(Integer.class)  //expect the list of elements as Integer type -> basically it converts the incoming elements to a list.
                .hasSize(4);    //check the size of the number of elements in the list.
    }


    @Test
    public void flux_approach3() {
        //Asserting by comparing the list of elements.

        List<Integer> expectedIntegers = Arrays.asList(1, 2, 3, 4);
        EntityExchangeResult<List<Integer>> listEntityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();    //it returns the the response received from the call of the API wrapped in ExchangeResult object.

        Assertions.assertEquals(expectedIntegers, listEntityExchangeResult.getResponseBody());
    }


    @Test
    public void flux_approach4() {
        //Validating with consumer.

        List<Integer> expectedIntegers = Arrays.asList(1, 2, 3, 4);
        EntityExchangeResult<List<Integer>> listEntityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response ->
                        Assertions.assertEquals(expectedIntegers, response.getResponseBody())
                );
    }


    @Test
    public void longStreamFlux() {
        //Testing the infinite stream by cancelling the stream.

        Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxinfinitestream")
                .accept(MediaType.valueOf(MediaType.APPLICATION_STREAM_JSON_VALUE))
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux )
                .expectNext(0l)
                .expectNext(1l) //expect the element as the value passed in the argument
                .expectNext(2l)
                .expectNext(3l)
                .thenCancel()   //calling the cancel() to stop the infinite stream.
                .verify();
    }

    @Test
    public void mono(){
        //working with mono.

        Integer expectedValue = 1;
        webTestClient.get().uri("/mono")
                .accept(MediaType.valueOf(MediaType.APPLICATION_STREAM_JSON_VALUE))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    Assertions.assertEquals(expectedValue,response.getResponseBody());
                });
    }
}
