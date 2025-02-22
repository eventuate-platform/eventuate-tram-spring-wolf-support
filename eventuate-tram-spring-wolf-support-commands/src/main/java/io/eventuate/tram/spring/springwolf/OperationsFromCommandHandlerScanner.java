package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.spring.commands.consumer.CommandClassExtractor;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
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

import static io.eventuate.tram.spring.springwolf.ChannelsFromCommandHandlerScanner.searchAppContextForCommandHandlers;

@org.springframework.stereotype.Component
public class OperationsFromCommandHandlerScanner implements EventuateTramOperationsScanner {

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  public Map<String, Operation> scan() {

    List<CommandHandlerInfo> commandHandlers = searchAppContextForCommandHandlers(ctx);
    return makeOperationsFromCommandHandlers(commandHandlers);

  }

  private Map<String, Operation> makeOperationsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    return commandHandlers
        .stream()
        .collect(Collectors.toMap(
            OperationsFromCommandHandlerScanner::getOperationId,
            ch -> makeOperationsFromCommandHandlers(ch.getEventuateCommandHandler().channel(), ch)
        ));
  }

  private Operation makeOperationsFromCommandHandlers(String channel, CommandHandlerInfo ch) {
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(getOperationId(ch))
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(List.of(SpringWolfUtils.makeMessageReferenceFromEventClass(CommandClassExtractor.extractCommandClass(ch.getMethod()))))
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

  private static String getOperationId(CommandHandlerInfo ch) {
    return ch.getMethod().getDeclaringClass().getName() + "." + ch.getMethod().getName();
  }

  private MessageReference makeMessageReference(Class<?> aClass) {
    MessageObject message = springWolfMessageFactory.makeMessageFromClass(aClass);
    return MessageReference.toComponentMessage(message);
  }


}
