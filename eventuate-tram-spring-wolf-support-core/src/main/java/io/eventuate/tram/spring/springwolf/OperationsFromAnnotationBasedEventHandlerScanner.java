package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventHandlerInfo;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import io.github.springwolf.asyncapi.v3.model.operation.OperationReply;
import io.github.springwolf.asyncapi.v3.model.operation.OperationReplyAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.eventuate.tram.spring.springwolf.ChannelsFromAnnotationBasedEventHandlerScanner.searchAppContextForDomainEventHandlers;

@org.springframework.stereotype.Component
public class OperationsFromAnnotationBasedEventHandlerScanner implements EventuateTramOperationsScanner{

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  public Map<String, Operation> scan() {

    List<EventuateDomainEventHandlerInfo> commandHandlers = searchAppContextForDomainEventHandlers(ctx);
    return makeOperationsFromCommandHandlers(commandHandlers);

  }

  private Map<String, Operation> makeOperationsFromCommandHandlers(List<EventuateDomainEventHandlerInfo> commandHandlers) {
    return commandHandlers
        .stream()
        .collect(Collectors.toMap(
            OperationsFromAnnotationBasedEventHandlerScanner::getOperationId,
            ch -> makeOperationsFromCommandHandlers(ch.getEventuateDomainEventHandler().channel(), ch)
        ));
  }

  private Operation makeOperationsFromCommandHandlers(String channel, EventuateDomainEventHandlerInfo ch) {
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(getOperationId(ch))
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(List.of(SpringWolfUtils.makeMessageReferenceFromEventClass(TypeParameterExtractor.extractTypeParameter(ch.getMethod(), DomainEvent.class))))
        .reply(OperationReply.builder()
            .messages(MessageClassScanner.findConcreteImplementorsOf(ch.getMethod().getReturnType()).stream()
                .map(this::makeMessageReference)
                .toList())
            .address(OperationReplyAddress.builder()
                .location("$message.header#/" + CommandMessageHeaders.REPLY_TO)
                .build())
            .build())
        .build();
  }

  private static String getOperationId(EventuateDomainEventHandlerInfo ch) {
    return ch.getMethod().getDeclaringClass().getName() + "." + ch.getMethod().getName();
  }

  private MessageReference makeMessageReference(Class<?> aClass) {
    MessageObject message = springWolfMessageFactory.makeMessageFromClass(aClass);
    return MessageReference.toComponentMessage(message);
  }


}
