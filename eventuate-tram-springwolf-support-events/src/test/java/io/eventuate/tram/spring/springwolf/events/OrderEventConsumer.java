package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.annotations.EventuateDomainEventHandler;

public class OrderEventConsumer {


  @EventuateDomainEventHandler(subscriberId = "orderServiceEventsConsumer", channel = "orderServiceEvents")
  public void handleOrderCreatedEvent(DomainEventEnvelope<OrderCreatedEvent> domainEventEnvelope) {

  }

}
