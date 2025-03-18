package io.eventuate.exampleapp.events.legacyconsumer;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventConsumerConfiguration {

    @Bean
    public OrderEventConsumer orderEventConsumer() {
        return new OrderEventConsumer();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(OrderEventConsumer orderEventConsumer,
                                                      DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("customerServiceEvents", orderEventConsumer.domainEventHandlers());
    }
}