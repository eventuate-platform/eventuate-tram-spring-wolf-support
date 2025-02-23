package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.spring.commands.consumer.CommandClassExtractor;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.Message;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChannelsFromCommandHandlerScanner implements EventuateTramChannelsScanner {

  private final SpringWolfMessageFactory springWolfMessageFactory;

  private final EventuateCommandDispatcher eventuateCommandDispatcher;

  @Autowired
  public ChannelsFromCommandHandlerScanner(EventuateCommandDispatcher eventuateCommandDispatcher,
                                           SpringWolfMessageFactory springWolfMessageFactory) {
    this.eventuateCommandDispatcher = eventuateCommandDispatcher;
    this.springWolfMessageFactory = springWolfMessageFactory;
  }

  @Override
  public Map<String, ChannelObject> scan() {
    List<CommandHandlerInfo> commandHandlers = eventuateCommandDispatcher.getCommandHandlers();
    return makeChannelsFromCommandHandlers(commandHandlers);
  }

  private Map<String, ChannelObject> makeChannelsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    return commandHandlers.stream()
        .collect(Collectors.groupingBy((CommandHandlerInfo eventuateCommandHandler) -> eventuateCommandHandler.getEventuateCommandHandler().channel()))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> makeChannelObject(entry.getKey(), entry.getValue())
        ));
  }

  private ChannelObject makeChannelObject(String key,  List<CommandHandlerInfo> commandHanders) {
    return ChannelObject.builder()
        .channelId(key)
        .messages(commandHanders.stream()
            .map(ch -> CommandClassExtractor.extractCommandClass(ch.getMethod()))
            .collect(Collectors.toMap(
                Class::getName, // key mapper
                this::makeMessageReference // value mapper
            )))
        .build();
  }

  private Message makeMessageReference(Class<? extends Command> aClass) {
    MessageObject message = springWolfMessageFactory.makeMessageFromClass(aClass);
    return MessageReference.toComponentMessage(message);
  }


}
