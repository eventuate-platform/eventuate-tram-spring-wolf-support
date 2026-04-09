package io.eventuate.exampleapp.events.common;

public record OrderCreatedEvent(long orderId, long customerId) implements OrderEvent {
}
