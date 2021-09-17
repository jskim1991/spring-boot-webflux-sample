package io.jay.springbootwebfluxsample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class FluxConfiguration {

    @Bean
    public Sinks.Many<Customer> sink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
