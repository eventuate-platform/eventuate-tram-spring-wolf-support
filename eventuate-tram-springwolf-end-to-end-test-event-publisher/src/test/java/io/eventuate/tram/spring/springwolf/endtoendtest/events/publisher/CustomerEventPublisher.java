package io.eventuate.tram.spring.springwolf.endtoendtest.events.publisher;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;

public interface CustomerEventPublisher extends DomainEventPublisherForAggregate<Customer, String, CustomerEvent> {
}
