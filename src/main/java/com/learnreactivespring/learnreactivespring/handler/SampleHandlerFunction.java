package com.learnreactivespring.learnreactivespring.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SampleHandlerFunction {
    public Mono<ServerResponse> flux(ServerRequest serverRequest) {
        //We have ServerResponse and ServerRequest in Reactive Specification -> which is on a similar lines to ServletRequest and ServletResponse of Servlet Specification.

        return ServerResponse.ok()  //Send a 200 Response
                .contentType(MediaType.APPLICATION_JSON)    //Send the content type as MediaType.APPLICATION_JSON
                .body(Flux.just(1, 2, 3, 4).log(), Integer.class);  //Send the Flux.just(1, 2, 3, 4) as body.
        //here we return the server response
    }

    public Mono<ServerResponse> mono(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(1).log(), Integer.class);
        //here we return the server response
    }
}
