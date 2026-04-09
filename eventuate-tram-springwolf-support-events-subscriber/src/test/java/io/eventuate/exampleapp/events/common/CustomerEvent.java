package io.eventuate.exampleapp.events.common;

import io.eventuate.tram.events.common.DomainEvent;

public interface CustomerEvent extends DomainEvent {
    String getId();
}