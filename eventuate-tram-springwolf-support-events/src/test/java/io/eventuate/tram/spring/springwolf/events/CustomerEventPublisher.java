package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.eventuate.tram.spring.springwolf.testing.Customer;

public interface CustomerEventPublisher extends DomainEventPublisherForAggregate<Customer, String, CustomerEvent> {
}
