package io.eventuate.exampleapp.events.common;

import io.eventuate.tram.events.common.DomainEvent;

public record OrderCreatedEvent(long orderId, long customerId) implements DomainEvent {
}
