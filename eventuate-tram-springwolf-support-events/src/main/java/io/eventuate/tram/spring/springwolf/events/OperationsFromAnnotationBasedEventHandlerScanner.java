package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventDispatcher;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventHandlerInfo;
import io.eventuate.tram.spring.springwolf.core.*;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OperationsFromAnnotationBasedEventHandlerScanner implements EventuateTramOperationsScanner {

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  private final EventuateDomainEventDispatcher eventuateDomainEventDispatcher;

  public OperationsFromAnnotationBasedEventHandlerScanner(EventuateDomainEventDispatcher eventuateDomainEventDispatcher) {
    this.eventuateDomainEventDispatcher = eventuateDomainEventDispatcher;
  }


  public ElementsWithClasses<Operation> scan() {
    List<EventuateDomainEventHandlerInfo> eventHandlers = eventuateDomainEventDispatcher.getEventHandlers();
    return ElementsWithClasses.make(makeOperationsFromEventHandlers(eventHandlers));
  }

  private Map<String, ElementWithClasses<Operation>> makeOperationsFromEventHandlers(List<EventuateDomainEventHandlerInfo> eventHandlers) {
    return eventHandlers
        .stream()
        .collect(Collectors.toMap(
            OperationsFromAnnotationBasedEventHandlerScanner::getOperationId,
            ch -> makeOperationFromEventHandler(ch.getChannel(), ch)
        ));
  }

  private ElementWithClasses<Operation> makeOperationFromEventHandler(String channel, EventuateDomainEventHandlerInfo eventHandlers) {
    Class<?> classz = TypeParameterExtractor.extractTypeParameter(eventHandlers.getMethod(), DomainEvent.class);
    Operation operation = Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(getOperationId(eventHandlers))
        .action(OperationAction.RECEIVE)
        .messages(List.of(SpringWolfUtils.makeMessageReference(classz)))
        .build();
    return new ElementWithClasses<>(operation, Set.of(classz));
  }

  private static String getOperationId(EventuateDomainEventHandlerInfo ch) {
    return ch.getMethod().getDeclaringClass().getName() + "." + ch.getMethod().getName();
  }



}
