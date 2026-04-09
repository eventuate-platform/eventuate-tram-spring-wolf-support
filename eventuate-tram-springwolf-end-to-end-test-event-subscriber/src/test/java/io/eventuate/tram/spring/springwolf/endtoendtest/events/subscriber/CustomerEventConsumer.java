package io.eventuate.tram.spring.springwolf.endtoendtest.events.subscriber;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.annotations.EventuateDomainEventHandler;

public class CustomerEventConsumer {

  @EventuateDomainEventHandler(subscriberId = "orderServiceEventsConsumer", channel = "io.eventuate.tram.spring.springwolf.endtoendtest.events.subscriber.Customer")
  public void handleCustomerCreatedEvent(DomainEventEnvelope<CustomerCreatedEvent> domainEventEnvelope) {

  }

}
