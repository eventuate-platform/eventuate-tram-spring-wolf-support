package io.eventuate.tram.spring.springwolf.application.requestasyncresponse;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import io.eventuate.tram.spring.springwolf.CommandClassExtractor;
import io.eventuate.tram.spring.springwolf.CommandHandlerInfo;
import io.eventuate.tram.spring.springwolf.EventuateCommandHandler;
import org.springframework.context.Lifecycle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventuateCommandDispatcher implements Lifecycle {
  private final SagaCommandDispatcherFactory sagaCommandDispatcherFactory;
  private List<CommandHandlerInfo> commandHandlers = new ArrayList<>();
  private boolean running = false;
  private List<SagaCommandDispatcher> dispatchers;

  public EventuateCommandDispatcher(SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
    this.sagaCommandDispatcherFactory = sagaCommandDispatcherFactory;
  }

  public void registerHandlerMethod(Object bean, EventuateCommandHandler eventuateCommandHandler, Method method) {
    commandHandlers.add(new CommandHandlerInfo(bean, eventuateCommandHandler, method));
  }

  @Override
  public void start() {
    Map<String, List<CommandHandlerInfo>> groupedCommandHandlers= commandHandlers.stream()
        .collect(Collectors.groupingBy(ch -> ch.eventuateCommandHandler().subscriberId()));
    this.dispatchers = groupedCommandHandlers.entrySet()
        .stream()
        .map(e -> sagaCommandDispatcherFactory
            .make(e.getKey(), makeCommandHandlers(e.getValue())))
        .toList();
    dispatchers.forEach(SagaCommandDispatcher::initialize);
    running = true;

  }

  private static CommandHandlers makeCommandHandlers(List<CommandHandlerInfo> commandHandlers) {
    Map<String, List<CommandHandlerInfo>> groupedByChannel = commandHandlers.stream().collect(Collectors.groupingBy(ch -> ch.eventuateCommandHandler().channel()));
    AtomicReference<SagaCommandHandlersBuilder> builder = new AtomicReference<>();
    groupedByChannel.forEach((channel, handlers) -> {
      builder.set(SagaCommandHandlersBuilder.fromChannel(channel));
      handlers.forEach(ch -> {
        builder.get().onMessage(commandClass(ch.method()), consumerFrom(ch));
      });
    });
    return builder.get().build();
  }

  private static Class<? extends Command> commandClass(Method method) {
    return CommandClassExtractor.extractCommandClass(method);
  }

  @Override
  public void stop() {
    // No close dispatchers.forEach(SagaCommandDispatcher::);
    running = false;
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  private static <T extends Command> Function<CommandMessage<T>, Message> consumerFrom(CommandHandlerInfo handler) {
    return cm -> {
      try {
        return (Message) handler.method().invoke(handler.bean(), cm);
      } catch (Exception e) {
        throw new RuntimeException("Error invoking command handler method", e);
      }
    };
  }
}
