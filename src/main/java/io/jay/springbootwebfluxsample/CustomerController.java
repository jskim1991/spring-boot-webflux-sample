package io.jay.springbootwebfluxsample;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.UUID;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final Sinks.Many<Customer> sink;

    public CustomerController(CustomerRepository customerRepository, Sinks.Many<Customer> sink) {
        this.customerRepository = customerRepository;
        this.sink = sink;
    }

    @GetMapping("/customer")
    public Flux<Customer> findAll() {
        return customerRepository.findAll().log();
    }

    @GetMapping("/customer/{id}")
    public Mono<Customer> findById(@PathVariable Long id) {
        return customerRepository.findById(id);
    }

//        @GetMapping(value = "/customer/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @GetMapping(value = "/customer/sse")
    public Flux<ServerSentEvent<Customer>> findAllSSE() {
        return sink.asFlux().map(c -> ServerSentEvent.builder(c).build())
                .doOnCancel(() -> sink.asFlux().blockLast());
    }

    @PostMapping("/customer")
    public Mono<Customer> save() {
        Customer customer = new Customer(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        return customerRepository.save(customer).doOnNext(c -> {
            sink.tryEmitNext(c);
        });
    }

    @GetMapping("/flux")
    public Flux<Integer> flux() {
        return Flux.range(1, 5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> fluxStream() {
        return Flux.range(1, 5).delayElements(Duration.ofSeconds(1)).log();
    }
}
