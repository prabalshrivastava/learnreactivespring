package com.learnreactivespring.learnreactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

//@@@2
@RestController
public class FluxAndMonoController {

    //go to - https://spring.io/reactive

    //@GetMapping is the router function
    @GetMapping("/flux")
    public Flux<Integer> returnFlux() { //Handler function is the function which is the body.
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();

        //By default, browser is a blocking client and it waits for the entire response to be received and then it displays it in JSON
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> returnFluxStream() {
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(2))
                .log();

        //By default, browser is a blocking client and it waits for the entire response to be received and then it displays it in JSON
    }


    @GetMapping(value = "/fluxinfinitestream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnFluxInfiniteStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }


    @GetMapping(value = "/mono", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<Integer> returnMono() {
        return Mono.just(1)
                .log();
    }
}
