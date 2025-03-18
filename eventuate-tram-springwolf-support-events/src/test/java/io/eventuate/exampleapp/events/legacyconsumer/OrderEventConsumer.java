package io.eventuate.exampleapp.events.legacyconsumer;

import io.eventuate.exampleapp.events.common.OrderCreatedEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;

public class OrderEventConsumer {


  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
        .forAggregateType("io.eventuate.exampleapp.events.common.Order")
        .onEvent(OrderCreatedEvent.class, this::handleOrderCreatedEvent)
        .build();
  }

  private void handleOrderCreatedEvent(DomainEventEnvelope<OrderCreatedEvent> domainEventEnvelope) {

  }

}
