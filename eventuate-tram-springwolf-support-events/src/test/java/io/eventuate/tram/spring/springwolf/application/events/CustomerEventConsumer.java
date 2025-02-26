package io.eventuate.tram.spring.springwolf.application.events;


import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import io.eventuate.tram.spring.springwolf.test.common.Customer;


public class CustomerEventConsumer {


  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
            .forAggregateType(Customer.class.getName())
            .onEvent(CustomerCreatedEvent.class, this::handleCustomerCreditReservedEvent)
            .build();
  }

  private void handleCustomerCreditReservedEvent(DomainEventEnvelope<CustomerCreatedEvent> domainEventEnvelope) {

  }


}
