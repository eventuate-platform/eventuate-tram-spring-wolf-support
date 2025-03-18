package io.eventuate.exampleapp.events.publisher;

import io.eventuate.exampleapp.events.common.Customer;
import io.eventuate.exampleapp.events.common.CustomerEvent;
import io.eventuate.tram.events.publisher.AbstractDomainEventPublisherForAggregateImpl;
import io.eventuate.tram.events.publisher.DomainEventPublisher;

public class CustomerEventPublisherImpl extends AbstractDomainEventPublisherForAggregateImpl<Customer, String, CustomerEvent> implements CustomerEventPublisher {
    public CustomerEventPublisherImpl(DomainEventPublisher domainEventPublisher) {
        super(Customer.class, Customer::getId, domainEventPublisher, CustomerEvent.class);
    }
}
