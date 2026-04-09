package io.eventuate.exampleapp.events.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerEventConsumerConfiguration {

    
    @Bean
    public CustomerEventConsumer customerEventConsumer() {
        return new CustomerEventConsumer();
    }
}