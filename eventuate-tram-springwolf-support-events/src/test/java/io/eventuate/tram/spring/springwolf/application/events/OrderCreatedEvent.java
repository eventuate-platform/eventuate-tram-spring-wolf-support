package io.eventuate.tram.spring.springwolf.application.events;

import io.eventuate.tram.events.common.DomainEvent;

public record OrderCreatedEvent(long orderId, long customerId) implements DomainEvent {
}
