package io.eventuate.exampleapp.events.consumer;


import io.eventuate.exampleapp.events.common.CustomerCreatedEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.annotations.EventuateDomainEventHandler;


public class CustomerEventConsumer {



  @EventuateDomainEventHandler(subscriberId = "orderServiceEventsConsumer", channel = "io.eventuate.exampleapp.events.common.Customer")
  public void handleCustomerCreatedEvent(DomainEventEnvelope<CustomerCreatedEvent> domainEventEnvelope) {

  }

}
