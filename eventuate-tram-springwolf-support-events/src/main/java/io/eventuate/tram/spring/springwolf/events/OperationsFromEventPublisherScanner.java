package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramOperationsScanner;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static io.eventuate.tram.spring.springwolf.core.MessageClassScanner.findConcreteImplementorsOf;

@Component
public class OperationsFromEventPublisherScanner implements EventuateTramOperationsScanner {

  private final List<DomainEventPublisherForAggregate> domainEventPublisherForAggregates;

  public OperationsFromEventPublisherScanner(List<DomainEventPublisherForAggregate> domainEventPublisherForAggregates) {
    this.domainEventPublisherForAggregates = domainEventPublisherForAggregates;
  }

  public ElementsWithClasses<Operation> scan() {
    return new ElementsWithClasses<>(domainEventPublisherForAggregates.stream()
        .collect(Collectors.toMap(
            ep -> ep.getClass().getName(),
            this::makeOperationFromEventPublisher
        )));
  }

  private Operation makeOperationFromEventPublisher(DomainEventPublisherForAggregate ep) {
    String channel = ep.getAggregateClass().getName();
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(ep.getClass().getName())
        .action(OperationAction.SEND)
        .messages(findConcreteImplementorsOf(ep.getEventBaseClass()).stream()
            .map(clasz -> MessageReference.toChannelMessage(channel, clasz.getName()))
            .toList())
        .build();
  }

}
