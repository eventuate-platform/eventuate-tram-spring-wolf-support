package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandler;
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

    Map<String, Operation> aggregateTypeToEvents = domainEventDispatchers.stream()
        .map(DomainEventDispatcher::getDomainEventHandlers)
        .flatMap(dehs -> dehs.getHandlers().stream())
        .collect(Collectors.groupingBy(DomainEventHandler::getAggregateType,
            Collectors.collectingAndThen(
                Collectors.toList(),
                OperationsFromEventHandlersScanner::makeOperationForDomainEventHandlers
            )));

    return new ElementsWithClasses<>(aggregateTypeToEvents);

  }

  public static Operation makeOperationForDomainEventHandlers(List<DomainEventHandler> eventHandlersForAggregate) {
    String aggregateType = eventHandlersForAggregate.get(0).getAggregateType();
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + aggregateType)
            .build())
        .operationId("operationId")
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(eventHandlersForAggregate.stream()
            .map(deh -> SpringWolfUtils.makeMessageReference(deh.getEventClass()))
            .toList())
        .build();
  }

}
