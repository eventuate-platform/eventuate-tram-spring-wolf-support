package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.stream.Collectors;

import static io.eventuate.tram.spring.springwolf.MessageClassScanner.findConcreteImplementorsOf;

public class OperationsFromEventPublisherScanner implements EventuateTramOperationsScanner {

  @Autowired
  private ApplicationContext ctx;

  public Map<String, Operation> scan() {
    return ctx.getBeansOfType(DomainEventPublisherForAggregate.class).values().stream()
        .collect(Collectors.toMap(
            ep -> ep.getClass().getName(),
            this::makeOperationFromEventPublisher
        ));
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
            .map(SpringWolfUtils::makeMessageReferenceFromEventClass)
            .toList())
        .build();
  }

}
