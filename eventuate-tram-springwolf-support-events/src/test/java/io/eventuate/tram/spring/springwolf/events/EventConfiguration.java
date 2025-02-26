package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    public CustomerEventPublisher customerEventPublisher(DomainEventPublisher domainEventPublisher) {
        return new CustomerEventPublisherImpl(domainEventPublisher);
    }

    @Bean
    public CustomerEventConsumer orderEventConsumer() {
        return new CustomerEventConsumer();
    }    @Bean

    public OrderEventConsumer customerEventConsumer() {
        return new OrderEventConsumer();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(CustomerEventConsumer customerEventConsumer,
                                                      DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("customerServiceEvents", customerEventConsumer.domainEventHandlers());
    }
}