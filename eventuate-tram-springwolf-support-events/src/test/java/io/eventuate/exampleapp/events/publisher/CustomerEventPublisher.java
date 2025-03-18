package io.eventuate.exampleapp.events.publisher;

import io.eventuate.exampleapp.events.common.Customer;
import io.eventuate.exampleapp.events.common.CustomerEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;

public interface CustomerEventPublisher extends DomainEventPublisherForAggregate<Customer, String, CustomerEvent> {
}
