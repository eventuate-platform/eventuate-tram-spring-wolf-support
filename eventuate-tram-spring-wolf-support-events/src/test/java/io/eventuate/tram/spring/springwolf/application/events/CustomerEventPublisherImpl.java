package io.eventuate.tram.spring.springwolf.application.events;

import io.eventuate.tram.events.publisher.AbstractDomainEventPublisherForAggregateImpl;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.springwolf.test.common.Customer;

public class CustomerEventPublisherImpl extends AbstractDomainEventPublisherForAggregateImpl<Customer, Long, CustomerEvent> implements CustomerEventPublisher {

    public CustomerEventPublisherImpl(DomainEventPublisher domainEventPublisher) {
        super(Customer.class, Customer::getId, domainEventPublisher, CustomerEvent.class);
    }
}
