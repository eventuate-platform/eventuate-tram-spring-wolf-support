package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.publisher.AbstractDomainEventPublisherForAggregateImpl;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.springwolf.testing.Customer;

public class CustomerEventPublisherImpl extends AbstractDomainEventPublisherForAggregateImpl<Customer, String, CustomerEvent> implements CustomerEventPublisher {
    public CustomerEventPublisherImpl(DomainEventPublisher domainEventPublisher) {
        super(Customer.class, Customer::getId, domainEventPublisher, CustomerEvent.class);
    }
}
