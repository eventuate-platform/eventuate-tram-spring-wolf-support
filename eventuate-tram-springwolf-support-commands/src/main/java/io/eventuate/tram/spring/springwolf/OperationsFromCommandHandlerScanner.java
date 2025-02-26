package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.common.TypeParameterExtractor;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
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

import static io.eventuate.tram.spring.springwolf.SetUtil.add;

@Component
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
            ch -> makeOperationsFromCommandHandlers(ch.getChannel(), ch)
        ));
    return ElementsWithClasses.make(operationsWithClasses);
  }

  private ElementWithClasses<Operation> makeOperationsFromCommandHandlers(String channel, CommandHandlerInfo ch) {
    Set<Class<?>> replyClasses = MessageClassScanner.findConcreteImplementorsOf(ch.getMethod().getReturnType());
    Class<? extends Command> commandClass = (Class<? extends Command>) TypeParameterExtractor.extractTypeParameter(ch.getMethod());

    Operation operation = Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(getOperationId(ch))
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(List.of(SpringWolfUtils.makeMessageReference(commandClass)))
        .reply(OperationReply.builder()
            .messages(replyClasses.stream()
                .map(SpringWolfUtils::makeMessageReference)
                .toList())
            .address(OperationReplyAddress.builder()
                .location("$message.header#/" + CommandMessageHeaders.REPLY_TO)
                .build())
            .build())
        .build();

    return new ElementWithClasses<>(operation, add(replyClasses, commandClass));
  }

  private static String getOperationId(CommandHandlerInfo ch) {
    return ch.getMethod().getDeclaringClass().getName() + "." + ch.getMethod().getName();
  }


}
