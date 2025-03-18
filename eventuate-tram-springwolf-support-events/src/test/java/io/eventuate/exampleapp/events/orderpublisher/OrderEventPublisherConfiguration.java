package io.eventuate.exampleapp.events.orderpublisher;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventPublisherConfiguration {

    @Bean
    public OrderEventPublisher OrderEventPublisher(DomainEventPublisher domainEventPublisher) {
        return new OrderEventPublisherImpl(domainEventPublisher);
    }

}