package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventDispatcher;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventHandlerInfo;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ChannelsFromAnnotationBasedEventHandlerScanner implements EventuateTramChannelsScanner {

  private final EventuateDomainEventDispatcher eventuateDomainEventDispatcher;
  
  public ChannelsFromAnnotationBasedEventHandlerScanner(EventuateDomainEventDispatcher eventuateDomainEventDispatcher) {
    this.eventuateDomainEventDispatcher = eventuateDomainEventDispatcher;
  }

  @Override
  public ElementsWithClasses<ChannelObject> scan() {
    List<EventuateDomainEventHandlerInfo> eventHandlers = eventuateDomainEventDispatcher.getEventHandlers();
    return makeChannelsFromEventHandlers(eventHandlers);
  }

  private ElementsWithClasses<ChannelObject> makeChannelsFromEventHandlers(List<EventuateDomainEventHandlerInfo> eventHandlers) {
    Map<String, ElementWithClasses<ChannelObject>> channelsWithClasses = eventHandlers.stream()
        .collect(Collectors.groupingBy((EventuateDomainEventHandlerInfo eventuateCommandHandler) -> eventuateCommandHandler.getEventuateDomainEventHandler().channel()))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> makeChannelObject(entry.getKey(), entry.getValue())
        ));
    return ElementsWithClasses.make(channelsWithClasses);
  }

  private ElementWithClasses<ChannelObject> makeChannelObject(String key,  List<EventuateDomainEventHandlerInfo> eventHandlers) {
    Set<Class<?>> eventClasses = eventHandlers.stream()
        .map(ch -> TypeParameterExtractor.extractTypeParameter(ch.getMethod(), DomainEvent.class))
        .collect(Collectors.toSet());
    ChannelObject channel = ChannelObject.builder()
        .channelId(key)
        .messages(eventClasses
            .stream()
            .collect(Collectors.toMap(
                Class::getName,
                SpringWolfUtils::makeMessageReference
            )))
        .build();
    return new ElementWithClasses<>(channel, eventClasses);
  }


}
