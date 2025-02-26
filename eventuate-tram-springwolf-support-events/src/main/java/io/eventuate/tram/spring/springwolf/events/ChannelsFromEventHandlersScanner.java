package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandler;
import io.eventuate.tram.spring.springwolf.core.ElementWithClasses;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramChannelsScanner;
import io.eventuate.tram.spring.springwolf.core.SpringWolfUtils;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ChannelsFromEventHandlersScanner implements EventuateTramChannelsScanner {

  private final List<DomainEventDispatcher> domainEventDispatchers;

  public ChannelsFromEventHandlersScanner(List<DomainEventDispatcher> domainEventDispatchers) {
    this.domainEventDispatchers = domainEventDispatchers;
  }

  public ElementsWithClasses<ChannelObject> scan() {

    Map<String, List<DomainEventHandler>> aggregateTypeToEvents = domainEventDispatchers.stream()
        .map(DomainEventDispatcher::getDomainEventHandlers)
        .flatMap(dehs -> dehs.getHandlers().stream())
        .collect(Collectors.groupingBy(DomainEventHandler::getAggregateType));

    Map<String, ElementWithClasses<ChannelObject>> channelsWithClasses = aggregateTypeToEvents.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> makeChannelObjectWithClasses(entry.getKey(), entry.getValue())
        ));

    return ElementsWithClasses.make(channelsWithClasses);
  }

  private ElementWithClasses<ChannelObject> makeChannelObjectWithClasses(String aggregateType, List<DomainEventHandler> eventHandlers) {
    Set<Class<?>> classes = eventHandlers.stream()
        .map(DomainEventHandler::getEventClass)
        .collect(Collectors.toSet());

    ChannelObject channelObject = ChannelObject.builder()
        .channelId(aggregateType)
        .messages(classes.stream()
            .collect(Collectors.toMap(
                Class::getName,
                SpringWolfUtils::makeMessageReference
            )))
        .build();

    return new ElementWithClasses<>(channelObject, classes);
  }
}
