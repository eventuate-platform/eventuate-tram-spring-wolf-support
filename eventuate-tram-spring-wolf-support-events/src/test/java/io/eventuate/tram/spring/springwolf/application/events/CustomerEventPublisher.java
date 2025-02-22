package io.eventuate.tram.spring.springwolf.application.events;


import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.eventuate.tram.spring.springwolf.test.common.Customer;

public interface CustomerEventPublisher extends DomainEventPublisherForAggregate<Customer, Long, CustomerEvent> {
}
