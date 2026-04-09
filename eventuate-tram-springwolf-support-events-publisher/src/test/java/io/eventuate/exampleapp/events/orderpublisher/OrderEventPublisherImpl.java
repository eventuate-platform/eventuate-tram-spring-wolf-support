package io.eventuate.exampleapp.events.orderpublisher;

import io.eventuate.exampleapp.events.common.Order;
import io.eventuate.exampleapp.events.common.OrderEvent;
import io.eventuate.tram.events.publisher.AbstractDomainEventPublisherForAggregateImpl;
import io.eventuate.tram.events.publisher.DomainEventPublisher;

public class OrderEventPublisherImpl extends AbstractDomainEventPublisherForAggregateImpl<Order, String, OrderEvent> implements OrderEventPublisher {
    public OrderEventPublisherImpl(DomainEventPublisher domainEventPublisher) {
        super(Order.class, Order::getId, domainEventPublisher, OrderEvent.class);
    }
}
