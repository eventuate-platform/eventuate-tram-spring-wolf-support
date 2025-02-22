package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

public class DomainEventHandlersRetriever {
  static List<DomainEventHandlers> getDomainEventHandlers(ApplicationContext ctx) {

    return ctx.getBeansOfType(DomainEventDispatcher.class).values().stream()
        .map(DomainEventDispatcher::getDomainEventHandlers)
        .collect(Collectors.toList());
  }

}
