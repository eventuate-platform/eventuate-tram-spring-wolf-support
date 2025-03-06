package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramChannelsScanner;
import io.eventuate.tram.spring.springwolf.core.MessageClassScanner;
import io.eventuate.tram.spring.springwolf.core.SpringWolfMessageFactory;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChannelsFromEventPublishersScanner implements EventuateTramChannelsScanner {

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  private final List<DomainEventPublisherForAggregate> eventPublishers;

  public ChannelsFromEventPublishersScanner(List<DomainEventPublisherForAggregate> eventPublishers) {
    this.eventPublishers = eventPublishers;
  }

  public ElementsWithClasses<ChannelObject> scan() {
    return new ElementsWithClasses<>(eventPublishers.stream()
        .collect(Collectors.toMap(
            ep -> ep.getAggregateClass().getName(),
            ep -> makeChannelObjectFromEventPublisher(ep.getAggregateClass().getName(), ep.getEventBaseClass())
        )));
  }

  public ChannelObject makeChannelObjectFromEventPublisher(String aggregateType, Class eventBaseClass) {
    return ChannelObject.builder()
        .channelId(aggregateType)
        .messages(MessageClassScanner.findConcreteImplementorsOf(eventBaseClass).stream()
            .collect(Collectors.toMap(
                Class::getName,
                this::makeMessageReference
            )))
        .build();
  }

  private MessageReference makeMessageReference(Class eventClass) {
    MessageObject message = springWolfMessageFactory.makeMessageFromClass(eventClass);
    return MessageReference.toComponentMessage(message);
  }

}
