package io.jay.springbootwebfluxsample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {CustomerController.class})
@Import(value = {FluxConfiguration.class})
public class CustomerControllerTests {

    @MockBean
    private CustomerRepository customerRepository;

    @Autowired
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer("first", "last");
        when(customerRepository.findById(1L))
                .thenReturn(Mono.just(customer));
    }

    @Test
    void test_findById_returnsMono() {
        webClient.get()
                .uri("/customer/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("first")
                .jsonPath("$.lastName").isEqualTo("last");
    }
}
