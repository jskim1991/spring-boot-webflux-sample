package io.jay.springbootwebfluxsample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataR2dbcTest
@AutoConfigureDataR2dbc
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void test_findById_returnsCustomer() {
        Customer customer = new Customer("first", "last");
        customerRepository.save(customer).block();


        Mono<Customer> result = customerRepository.findById(1L);


        Customer returnedCustomer = result.block();
        assertThat(returnedCustomer.getId(), equalTo(1L));
        assertThat(returnedCustomer.getFirstName(), equalTo("first"));
        assertThat(returnedCustomer.getLastName(), equalTo("last"));

        StepVerifier.create(customerRepository.findById(1L))
                .assertNext(c -> {
                    assertThat(c.getId(), equalTo(1L));
                    assertThat(c.getFirstName(), equalTo("first"));
                    assertThat(c.getLastName(), equalTo("last"));
                })
                .verifyComplete();
    }
}
