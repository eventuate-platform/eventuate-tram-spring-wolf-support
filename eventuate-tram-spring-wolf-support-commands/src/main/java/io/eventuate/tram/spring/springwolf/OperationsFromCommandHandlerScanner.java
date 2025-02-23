package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.spring.commands.consumer.CommandClassExtractor;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import io.github.springwolf.asyncapi.v3.model.operation.OperationReply;
import io.github.springwolf.asyncapi.v3.model.operation.OperationReplyAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.eventuate.tram.spring.springwolf.ChannelsFromCommandHandlerScanner.searchAppContextForCommandHandlers;
import static io.eventuate.tram.spring.springwolf.SetUtil.add;

@org.springframework.stereotype.Component
public class OperationsFromCommandHandlerScanner implements EventuateTramOperationsScanner {

  @Autowired
  private ApplicationContext ctx;

  @Override
  public OperationsWithClasses scan() {
    List<CommandHandlerInfo> commandHandlers = searchAppContextForCommandHandlers(ctx);
    return makeOperationsFromCommandHandlers(commandHandlers);
  }

  private OperationsWithClasses makeOperationsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    Map<String, OperationWithClasses> operationsWithClasses = commandHandlers
        .stream()
        .collect(Collectors.toMap(
            OperationsFromCommandHandlerScanner::getOperationId,
            ch -> makeOperationsFromCommandHandlers(ch.getEventuateCommandHandler().channel(), ch)
        ));
    return OperationsWithClasses.make(operationsWithClasses);
  }

  private OperationWithClasses makeOperationsFromCommandHandlers(String channel, CommandHandlerInfo ch) {
    Set<Class<?>> replyClasses = MessageClassScanner.findConcreteImplementorsOf(ch.getMethod().getReturnType());
    Class<? extends Command> commandClass = CommandClassExtractor.extractCommandClass(ch.getMethod());

    Operation operation = Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId(getOperationId(ch))
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(List.of(makeMessageReference(commandClass)))
        .reply(OperationReply.builder()
            .messages(replyClasses.stream()
                .map(this::makeMessageReference)
                .toList())
            .address(OperationReplyAddress.builder()
                .location("$message.header#/" + CommandMessageHeaders.REPLY_TO)
                .build())
            .build())
        .build();

    return new OperationWithClasses(operation, add(replyClasses, commandClass));
  }

  private static String getOperationId(CommandHandlerInfo ch) {
    return ch.getMethod().getDeclaringClass().getName() + "." + ch.getMethod().getName();
  }

  private MessageReference makeMessageReference(Class<?> aClass) {
    // MessageObject message = springWolfMessageFactory.makeMessageFromClass(aClass);
    return MessageReference.toComponentMessage(aClass.getName());
  }


}
