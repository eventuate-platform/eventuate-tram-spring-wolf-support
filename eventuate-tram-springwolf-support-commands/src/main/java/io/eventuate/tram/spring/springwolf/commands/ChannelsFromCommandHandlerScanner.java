package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.common.TypeParameterExtractor;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramChannelsScanner;
import io.eventuate.tram.spring.springwolf.core.SpringWolfMessageFactory;
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
  public ElementsWithClasses<ChannelObject> scan() {
    List<CommandHandlerInfo> commandHandlers = eventuateCommandDispatcher.getCommandHandlers();
    return new ElementsWithClasses<>(makeChannelsFromCommandHandlers(commandHandlers));
  }

  private Map<String, ChannelObject> makeChannelsFromCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    return commandHandlers.stream()
        .collect(Collectors.groupingBy(
            CommandHandlerInfo::getChannel,
            Collectors.collectingAndThen(
                Collectors.toList(),
                this::makeChannelObject
            )
        ));
  }

  private ChannelObject makeChannelObject(List<CommandHandlerInfo> commandHanders) {
    String key = commandHanders.get(0).getChannel();
    return ChannelObject.builder()
        .channelId(key)
        .messages(commandHanders.stream()
            .map(commandHandler -> (Class<Command>)TypeParameterExtractor.extractTypeParameter(commandHandler.getMethod()))
            .collect(Collectors.toMap(
                Class::getName,
                this::makeMessageReference
            )))
        .build();
  }

  private Message makeMessageReference(Class<? extends Command> aClass) {
    MessageObject message = springWolfMessageFactory.makeMessageFromClass(aClass);
    return MessageReference.toComponentMessage(message);
  }


}
