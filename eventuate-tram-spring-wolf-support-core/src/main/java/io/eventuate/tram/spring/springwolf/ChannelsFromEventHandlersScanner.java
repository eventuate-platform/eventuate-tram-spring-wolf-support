package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.subscriber.DomainEventHandler;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelsFromEventHandlersScanner implements EventuateTramChannelsScanner{

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  public Map<String, ChannelObject> scan() {
    List<DomainEventHandlers> domainEventHandlers = DomainEventDispatcherHacks.getDomainEventHandlers(ctx);

    Map<String, List<DomainEventHandler>> aggregateTypeToEvents = domainEventHandlers.stream()
        .flatMap(dehs -> dehs.getHandlers().stream())
        .collect(Collectors.groupingBy(DomainEventHandler::getAggregateType));

    return aggregateTypeToEvents.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> makeChannelObject(entry.getKey(), entry.getValue())
        ));
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
