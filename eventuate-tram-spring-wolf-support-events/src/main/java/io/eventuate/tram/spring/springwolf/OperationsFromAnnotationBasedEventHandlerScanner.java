package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventHandlerInfo;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.eventuate.tram.spring.springwolf.ChannelsFromAnnotationBasedEventHandlerScanner.searchAppContextForDomainEventHandlers;

@org.springframework.stereotype.Component
public class OperationsFromAnnotationBasedEventHandlerScanner implements EventuateTramOperationsScanner{

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  public ElementsWithClasses<Operation> scan() {
    List<EventuateDomainEventHandlerInfo> commandHandlers = searchAppContextForDomainEventHandlers(ctx);
    return ElementsWithClasses.make(makeOperationsFromEventHandlers(commandHandlers));
  }

  private Map<String, ElementWithClasses<Operation>> makeOperationsFromEventHandlers(List<EventuateDomainEventHandlerInfo> commandHandlers) {
    return commandHandlers
        .stream()
        .collect(Collectors.toMap(
            OperationsFromAnnotationBasedEventHandlerScanner::getOperationId,
            ch -> makeOperationFromEventHandler(ch.getEventuateDomainEventHandler().channel(), ch)
        ));
  }

  private ElementWithClasses<Operation> makeOperationFromEventHandler(String channel, EventuateDomainEventHandlerInfo ch) {
    Class<?> classz = TypeParameterExtractor.extractTypeParameter(ch.getMethod(), DomainEvent.class);
    Operation operation = Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(getOperationId(ch))
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(List.of(SpringWolfUtils.makeMessageReference(classz)))
        .build();
    return new ElementWithClasses<>(operation, Set.of(classz));
  }

  private static String getOperationId(EventuateDomainEventHandlerInfo ch) {
    return ch.getMethod().getDeclaringClass().getName() + "." + ch.getMethod().getName();
  }



}
