package io.eventuate.tram.spring.springwolf.application.events;

import io.eventuate.tram.events.common.DomainEvent;

public interface CustomerEvent extends DomainEvent {

    String getId();
}