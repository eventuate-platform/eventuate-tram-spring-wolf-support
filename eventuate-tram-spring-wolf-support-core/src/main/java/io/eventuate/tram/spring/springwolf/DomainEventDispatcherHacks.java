package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

public class DomainEventDispatcherHacks {
  static List<DomainEventHandlers> getDomainEventHandlers(ApplicationContext ctx) {
    List<DomainEventDispatcher> dispatchers = ctx.getBeansOfType(DomainEventDispatcher.class).values().stream().toList();

    return dispatchers.stream()
        .map(DomainEventDispatcher::getDomainEventHandlers)
        .collect(Collectors.toList());
  }

}
