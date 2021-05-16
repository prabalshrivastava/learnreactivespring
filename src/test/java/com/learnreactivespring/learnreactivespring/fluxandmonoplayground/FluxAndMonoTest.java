package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

//@@@1
public class FluxAndMonoTest {

    //Why spring reactive?
    //1. Traditional non-reactive code use 2 times the resources.
    //2. Its scalable.
    //3. It's enables to code easily in functional way.
    //4. This is like assembly of pipelines which are cold when data doesn't flow and hot when data does.
    @Test
    public void fluxTest() {
        //A Flux is a publisher of a sequence of events of a specific POJO type, so it is generic, i.e. Flux<T> is a publisher of T.
        // Flux has some static convenience methods to create instances of itself from a variety of sources. For example, to create a Flux from an array:
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring");    //these three elements are emitted by flux as events as onNext() method

        stringFlux.subscribe(s -> System.out.println("-----------------------> : " + s));   //when you subscribe thats when the flux starts emitting the elements
    }

    @Test
    public void fluxTest_withLog() {
        //There are a lot of methods on a Flux and nearly all of them are operators.
        // to ask for the internal events inside a Flux to be logged to standard out, you can call the .log() method.
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();  //log function is used to log the events flowing through the stream
//        stringFlux.subscribe(System.out::println);
        stringFlux.subscribe(s -> System.out.println("-----------------------> : " + s));   //when you subscribe thats when the flux starts emitting the elements
        stringFlux.subscribe(s -> System.out.println("-----------------------> : " + s));   //when you subscribe thats when the flux starts emitting the elements
    }


    @Test
    public void fluxTest_withMap() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log()    //this will show how the data flows through the stream
//                .map(String::toUpperCase)   //we transformed the strings in the input by converting them to upper case.
                .map(s -> s.toUpperCase())    //equivalent of above line
                .log();

        stringFlux.subscribe(System.out::println);
    }

    @Test
    public void fluxTest_coldVsHot() {
        //no data have been processed yet. Nothing has even been logged because literally, nothing happened (try it and you will see). Calling operators on a Flux amounts to building a plan of execution for later.
        // It is completely declarative, and it’s why people call it "functional".
        // The logic implemented in the operators is only executed when data starts to flow, and that doesn’t happen until someone subscribes to the Flux (or equivalently to the Publisher).
        //cold -> streams of data are not flowing through it
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log()
                .map(String::toUpperCase)
                .log();

        //hot -> streams of data are flowing through it
        stringFlux.subscribe(s -> System.out.println("-----------------------> : " + s));
    }

    @Test
    public void fluxTest_withConcat() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.just("First Reactive Spring")) //concatWith concat the fluxes
                .log();

        stringFlux.subscribe(System.out::println);
    }


    @Test
    public void fluxTest_withError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
//                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        stringFlux.subscribe(System.out::println,
                throwable -> System.err.println(throwable)); //without method reference
//                System.err::println); //subscribe method has many overloaded version of methods -> here we have handled the exception
    }

    @Test
    public void fluxTest_afterError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After Error"))   //this part doesn't gets emitted since its called after the error
                .log();

        stringFlux.subscribe(System.out::println,
                throwable -> System.err.println(throwable),
                () -> System.out.println("Completed")   //this completed event doesn't gets called since the error event occurred
        ); //when you subscribe thats when the flux starts emitting the elements
    }


    @Test
    public void fluxTestElements_usingStepVerifier() {
        //How to verify Elements using StepVerifier
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();  //verify complete is the method that enables the flow of elements and expects positive results ie it does not expect an error
    }

    @Test
    public void fluxTestElements_usingStepVerifier_differentOrder() {
        //What happens if the order of elements is not what we expect in StepVerifier
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring")
                .expectNext("Reactive Spring")
                .verifyComplete();
        //this test case fails as the order of emitted elements is not consistent with what is expected in our test cases
    }

    @Test
    public void fluxTestElements_validateErrorWithStepVerifier() {
        //How to test and validate if the error occurs in StepVerifier
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectError(RuntimeException.class)
                .verify();  //for checking errors verify() is used instead of verifyComplete()
    }

    @Test
    public void fluxTestElements_validateErrorMessageWithStepVerifier() {
        //How to test and validate if the error occurs in StepVerifier and check the error message.
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectErrorMessage("Exception Occurred")   //expectErrorMessage checks for the message "Exception Occurred"
                .verify();
    }

    @Test
    public void fluxTestElementsCount_withError() {
        //How to test and validate the count of elements(when error occurs) emitted by the Flux in StepVerifier,
        Flux<String> stringFlux = Flux
                .just("Spring", "Spring Boot", "Reactive Spring")
//                .just("Spring", "Spring Boot")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3) //check for the count of elements
                .expectErrorMessage("Exception Occurred")
                .verify();
    }


    @Test
    public void fluxTestElements_combiningExpectNext() {
        //Tired of writing expectNext n number of times?
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")));

        StepVerifier.create(stringFlux.log())   //another way to add to existing stream
                .expectNext("Spring", "Spring Boot", "Reactive Spring") //you can pass the elements as a varargs.
                .expectErrorMessage("Exception Occurred")   //expectErrorMessage checks for the message "Exception Occurred"
                .verify();
    }


    @Test
    public void monoTest() {
        //Reactor has a Mono type representing a single valued or empty Flux.
        // Mono has a very similar API to Flux but more focused because not all operators make sense for single-valued sequences.
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }


    @Test
    public void monoTest_Error() {
        //Mono.error() is used to create an exception Mono
        Mono<Object> stringMono = Mono.error(new RuntimeException("Exception Occurred")).log();

        StepVerifier.create(stringMono)
                .expectError(RuntimeException.class)
                .verify();
    }
}
