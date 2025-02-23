package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandler;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OperationsFromEventHandlersScanner implements EventuateTramOperationsScanner {

  private final List<DomainEventDispatcher> domainEventDispatchers;

  public OperationsFromEventHandlersScanner(List<DomainEventDispatcher> domainEventDispatchers) {
    this.domainEventDispatchers = domainEventDispatchers;
  }

  public ElementsWithClasses<Operation> scan() {

    List<DomainEventHandlers> domainEventHandlers = domainEventDispatchers.stream()
        .map(DomainEventDispatcher::getDomainEventHandlers)
        .toList();

    Map<String, List<DomainEventHandler>> aggregateTypeToEvents = domainEventHandlers.stream()
        .flatMap(dehs -> dehs.getHandlers().stream())
        .collect(Collectors.groupingBy(DomainEventHandler::getAggregateType));


    return new ElementsWithClasses<>(aggregateTypeToEvents.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey, // key mapper
            entry -> makeOperationForDomainEventHandlers(entry.getKey(), entry.getValue()) // value mapper
        )));
  }

  public static Operation makeOperationForDomainEventHandlers(String aggregateType, List<DomainEventHandler> eventHandlers) {
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + aggregateType)
            .build())
        .operationId("operationId")
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(eventHandlers.stream()
            .map(deh -> SpringWolfUtils.makeMessageReference(deh.getEventClass()))
            .toList())
        .build();
  }

}
