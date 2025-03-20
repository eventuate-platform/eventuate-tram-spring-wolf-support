package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.common.TypeParameterExtractor;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.eventuate.tram.spring.springwolf.core.ElementWithClasses;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramOperationsScanner;
import io.eventuate.tram.spring.springwolf.core.MessageClassScanner;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import io.github.springwolf.asyncapi.v3.model.operation.OperationReply;
import io.github.springwolf.asyncapi.v3.model.operation.OperationReplyAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.eventuate.tram.spring.springwolf.core.SetUtil.add;

public class OperationsFromCommandHandlerScanner implements EventuateTramOperationsScanner {

  private final EventuateCommandDispatcher eventuateCommandDispatcher;

  @Autowired
  public OperationsFromCommandHandlerScanner(EventuateCommandDispatcher eventuateCommandDispatcher) {
    this.eventuateCommandDispatcher = eventuateCommandDispatcher;
  }

  @Override
  public ElementsWithClasses<Operation> scan() {
    List<CommandHandlerInfo> commandHandlers = eventuateCommandDispatcher.getCommandHandlers();
    return makeOperationsFromCommandHandlers(commandHandlers);
  }

  private ElementsWithClasses<Operation> makeOperationsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    Map<String, ElementWithClasses<Operation>> operationsWithClasses = commandHandlers
        .stream()
        .collect(Collectors.toMap(
            OperationsFromCommandHandlerScanner::getOperationId,
            ch -> makeOperationFromCommandHandler(ch.getChannel(), ch)
        ));
    return ElementsWithClasses.make(operationsWithClasses);
  }

  private ElementWithClasses<Operation> makeOperationFromCommandHandler(String channel, CommandHandlerInfo ch) {
    Set<Class<?>> replyClasses = MessageClassScanner.findConcreteImplementorsOf(ch.getMethod().getReturnType());
    Class<? extends Command> commandClass = (Class<? extends Command>) TypeParameterExtractor.extractTypeParameter(ch.getMethod());
    String replyChannel = OperationsFromCommandHandlerScanner.getOperationId(ch) + "-reply";
    Operation operation = Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(getOperationId(ch))
        .action(OperationAction.RECEIVE)
//
//        .bindings(Map.of("eventuate-outbox", KafkaOperationBinding.builder()
//            .groupId(SchemaObject.builder().constValue("Foo").build())
//            .build()))

        .messages(List.of(MessageReference.toChannelMessage(channel, commandClass.getName())))
        .reply(OperationReply.builder()
            .messages(replyClasses.stream()
                .map(clasz -> MessageReference.toChannelMessage(replyChannel, clasz.getName()))
                .toList())
            .channel(ChannelReference.builder()
                .ref("#/channels/" + replyChannel)
                .build())
            .address(OperationReplyAddress.builder()
                .location("$message.header#/" + CommandMessageHeaders.REPLY_TO)
                .build())
            .build())
        .build();

    return new ElementWithClasses<>(operation, add(replyClasses, commandClass));
  }

  public static String getOperationId(CommandHandlerInfo ch) {
    return ch.getMethod().getDeclaringClass().getName() + "." + ch.getMethod().getName();
  }


}
