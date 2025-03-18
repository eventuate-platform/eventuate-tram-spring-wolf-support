package io.eventuate.exampleapp.events.orderpublisher;

import io.eventuate.exampleapp.events.common.Order;
import io.eventuate.exampleapp.events.common.OrderEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;

public interface OrderEventPublisher extends DomainEventPublisherForAggregate<Order, String, OrderEvent> {
}
