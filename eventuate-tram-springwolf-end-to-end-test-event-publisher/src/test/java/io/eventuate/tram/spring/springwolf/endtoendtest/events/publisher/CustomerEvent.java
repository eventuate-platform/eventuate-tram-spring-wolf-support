package io.eventuate.tram.spring.springwolf.endtoendtest.events.publisher;

import io.eventuate.tram.events.common.DomainEvent;

public interface CustomerEvent extends DomainEvent {
    String getId();
}
