package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.eventuate.tram.spring.springwolf.ChannelsFromCommandHandlerScanner.searchAppContextForCommandHandlers;

public class OperationsFromCommandHandlerScanner implements EventuateTramOperationsScanner{

  @Autowired
  private ApplicationContext ctx;

  public Map<String, Operation> scan() {

    List<CommandHandlerInfo> commandHandlers = searchAppContextForCommandHandlers(ctx);
    return makeOperationsFromCommandHandlers(commandHandlers);

  }

  private Map<String, Operation> makeOperationsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    return commandHandlers.stream()
        .collect(Collectors.groupingBy((CommandHandlerInfo eventuateCommandHandler) -> eventuateCommandHandler.eventuateCommandHandler().channel()))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> makeOperationsFromCommandHandlers(entry.getKey(), entry.getValue())
        ));
  }

  private Operation makeOperationsFromCommandHandlers(String channel, List<CommandHandlerInfo> commandHandlers) {
    return Operation.builder()
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .operationId("commandHandlers-" + channel)
        .description("my event handler")
        .action(OperationAction.RECEIVE)
        .messages(commandHandlers.stream()
            .map(ch -> SpringWolfUtils.makeMessageReferenceFromEventClass(CommandClassExtractor.extractCommandClass(ch.method())))
            .toList())
        .build();
  }



}
