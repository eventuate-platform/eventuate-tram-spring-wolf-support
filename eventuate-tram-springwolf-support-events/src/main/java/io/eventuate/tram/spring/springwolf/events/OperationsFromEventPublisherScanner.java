package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramOperationsScanner;
import io.eventuate.tram.spring.springwolf.core.SpringWolfUtils;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
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
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + ep.getAggregateClass().getName())
            .build())
        .operationId(ep.getClass().getName())
        .description("my event sender")
        .action(OperationAction.SEND)
        .messages(findConcreteImplementorsOf(ep.getEventBaseClass()).stream()
            .map(SpringWolfUtils::makeMessageReference)
            .toList())
        .build();
  }

}
