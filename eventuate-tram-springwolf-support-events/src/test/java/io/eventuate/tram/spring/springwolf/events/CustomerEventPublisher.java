package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;

public interface CustomerEventPublisher extends DomainEventPublisherForAggregate<Customer, String, CustomerEvent> {
}
