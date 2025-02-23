package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandler;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
public class ChannelsFromEventHandlersScanner implements EventuateTramChannelsScanner{

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  private final List<DomainEventDispatcher> domainEventDispatchers;

  public ChannelsFromEventHandlersScanner(List<DomainEventDispatcher> domainEventDispatchers) {
    this.domainEventDispatchers = domainEventDispatchers;
  }

  public ElementsWithClasses<ChannelObject> scan() {

    List<DomainEventHandlers> domainEventHandlers = domainEventDispatchers.stream()
        .map(DomainEventDispatcher::getDomainEventHandlers)
        .toList();

    Map<String, List<DomainEventHandler>> aggregateTypeToEvents = domainEventHandlers.stream()
        .flatMap(dehs -> dehs.getHandlers().stream())
        .collect(Collectors.groupingBy(DomainEventHandler::getAggregateType));

    return new ElementsWithClasses<>(aggregateTypeToEvents.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> makeChannelObject(entry.getKey(), entry.getValue())
        )));
  }

  private ChannelObject makeChannelObject(String aggregateType, List<DomainEventHandler> eventHandlers) {
    return ChannelObject.builder()
        .channelId(aggregateType)
        .messages(eventHandlers.stream()
            .collect(Collectors.toMap(
                deh -> deh.getEventClass().getName(), // key mapper
                this::makeMessageReference // value mapper
            )))
        .build();
  }

  private MessageReference makeMessageReference(DomainEventHandler deh) {
    MessageObject message = springWolfMessageFactory.makeMessageFromClass(deh.getEventClass());
    return MessageReference.toComponentMessage(message);
  }
}
