package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.common.TypeParameterExtractor;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.eventuate.tram.spring.springwolf.core.ElementWithClasses;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramChannelsScanner;
import io.eventuate.tram.spring.springwolf.core.MessageClassScanner;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.Message;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ChannelsFromCommandHandlerScanner implements EventuateTramChannelsScanner {

  private final EventuateCommandDispatcher eventuateCommandDispatcher;

  @Autowired
  public ChannelsFromCommandHandlerScanner(EventuateCommandDispatcher eventuateCommandDispatcher) {
    this.eventuateCommandDispatcher = eventuateCommandDispatcher;
  }

  @Override
  public ElementsWithClasses<ChannelObject> scan() {
    List<CommandHandlerInfo> commandHandlers = eventuateCommandDispatcher.getCommandHandlers();
    var channels = makeChannelsFromCommandHandlers(commandHandlers);
    var replyChannels = makeReplyChannelsFromCommandHandlers(commandHandlers);
    Map<String, ElementWithClasses<ChannelObject>> result = new HashMap<>();
    result.putAll(channels);
    result.putAll(replyChannels);
    return ElementsWithClasses.make(result);
  }


  private Map<String, ElementWithClasses<ChannelObject>> makeChannelsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    return commandHandlers.stream()
        .collect(Collectors.groupingBy(
            CommandHandlerInfo::getChannel,
            Collectors.collectingAndThen(
                Collectors.toList(),
                this::makeChannelObject
            )
        ));
  }

  private ElementWithClasses<ChannelObject> makeChannelObject(List<CommandHandlerInfo> commandHanders) {
    String key = commandHanders.get(0).getChannel();
    Set<Class<?>> commandClasses = commandHanders.stream()
        .map(commandHandler -> (Class<Command>)TypeParameterExtractor.extractTypeParameter(commandHandler.getMethod()))
        .collect(Collectors.toSet());

    ChannelObject channel = ChannelObject.builder()
        .channelId(key)
        .address(key)
//        .bindings(Map.of("eventuate-outbox", KafkaChannelBinding.builder()
//                .topic(key)
//            .build()))
        .messages(commandHanders.stream()
            .map(commandHandler -> (Class<Command>) TypeParameterExtractor.extractTypeParameter(commandHandler.getMethod()))
            .collect(Collectors.toMap(
                Class::getName,
                this::makeMessageReference
            )))
        .build();
    return new ElementWithClasses<>(channel, commandClasses);
  }

  private Message makeMessageReference(Class<?> aClass) {
    return MessageReference.toComponentMessage(aClass.getName());
  }

  private Map<String, ElementWithClasses<ChannelObject>> makeReplyChannelsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    Map<String, ElementWithClasses<ChannelObject>> operationsWithClasses = commandHandlers
        .stream()
        .collect(Collectors.toMap(
            ch-> OperationsFromCommandHandlerScanner.getOperationId(ch) + "-reply",
            ch -> makeReplyChannelFromCommandHandler(OperationsFromCommandHandlerScanner.getOperationId(ch) + "-reply", ch)
        ));
    return operationsWithClasses;
  }

  private ElementWithClasses<ChannelObject> makeReplyChannelFromCommandHandler(String replyChannelName, CommandHandlerInfo ch) {
    Set<Class<?>> replyClasses = MessageClassScanner.findConcreteImplementorsOf(ch.getMethod().getReturnType());
    ChannelObject channel = ChannelObject.builder()
        .channelId(replyChannelName)
        .address(replyChannelName + "-dummy") // Make SpringUI happy
        .messages(replyClasses.stream()
            .collect(Collectors.toMap(
                Class::getName,
                this::makeMessageReference)))
        .build();
    return new ElementWithClasses<>(channel, replyClasses);
  }

}
