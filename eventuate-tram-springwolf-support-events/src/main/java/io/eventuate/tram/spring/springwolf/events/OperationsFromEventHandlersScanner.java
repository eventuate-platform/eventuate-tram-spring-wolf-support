package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandler;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramOperationsScanner;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OperationsFromEventHandlersScanner implements EventuateTramOperationsScanner {

  private final List<DomainEventDispatcher> domainEventDispatchers;

  public OperationsFromEventHandlersScanner(List<DomainEventDispatcher> domainEventDispatchers) {
    this.domainEventDispatchers = domainEventDispatchers;
  }

  public ElementsWithClasses<Operation> scan() {

    Map<String, Operation> x = domainEventDispatchers.stream()
        .flatMap(ded -> {
          String eventDispatcherId = ded.getEventDispatcherId();
          return ded.getDomainEventHandlers().getHandlers().stream()
              .collect(Collectors.groupingBy(DomainEventHandler::getAggregateType))
              .values().stream()
              .map(eventHandlersForAggregate -> makeOperationForDomainEventHandlers(eventDispatcherId, eventHandlersForAggregate));
        })
        .collect(Collectors.toMap(Operation::getOperationId, Function.identity()));
    return new ElementsWithClasses<>(x);

  }

  private static Operation makeOperationForDomainEventHandlers(String eventDispatcherId, List<DomainEventHandler> eventHandlersForAggregate) {
    String aggregateType = eventHandlersForAggregate.get(0).getAggregateType();
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + aggregateType)
            .build())
        .operationId("receive-" + eventDispatcherId + "-" + aggregateType)
        .action(OperationAction.RECEIVE)
        .messages(eventHandlersForAggregate.stream()
            .map(DomainEventHandler::getEventClass)
            .map(clasz -> MessageReference.toChannelMessage(aggregateType, clasz.getName()))
            .toList())
        .build();
  }

}
