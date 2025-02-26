package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.common.DomainEvent;

public interface CustomerEvent extends DomainEvent {
    String getId();
}