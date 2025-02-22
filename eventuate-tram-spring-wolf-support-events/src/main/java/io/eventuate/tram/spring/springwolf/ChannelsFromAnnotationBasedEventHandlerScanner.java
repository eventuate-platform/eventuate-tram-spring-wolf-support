package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.subscriber.annotations.EventuateDomainEventHandler;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventHandlerInfo;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.Message;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChannelsFromAnnotationBasedEventHandlerScanner implements EventuateTramChannelsScanner {

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;

  @Override
  public Map<String, ChannelObject> scan() {
    List<EventuateDomainEventHandlerInfo> commandHandlers = searchAppContextForDomainEventHandlers(ctx);
    Map<String, ChannelObject> channels = makeChannelsFromCommandHandlers(commandHandlers);
    return channels;
  }

  private Map<String, ChannelObject> makeChannelsFromCommandHandlers(List<EventuateDomainEventHandlerInfo> commandHandlers) {
    return commandHandlers.stream()
        .collect(Collectors.groupingBy((EventuateDomainEventHandlerInfo eventuateCommandHandler) -> eventuateCommandHandler.getEventuateDomainEventHandler().channel()))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> makeChannelObject(entry.getKey(), entry.getValue())
        ));
  }

  private ChannelObject makeChannelObject(String key,  List<EventuateDomainEventHandlerInfo> commandHanders) {
    return ChannelObject.builder()
        .channelId(key)
        .messages(commandHanders.stream()
            .map(ch -> TypeParameterExtractor.extractTypeParameter(ch.getMethod(), DomainEvent.class))
            .collect(Collectors.toMap(
                Class::getName, // key mapper
                this::makeMessageReference // value mapper
            )))
        .build();
  }

  private Message makeMessageReference(Class<?> aClass) {
    MessageObject message = springWolfMessageFactory.makeMessageFromClass(aClass);
    return MessageReference.toComponentMessage(message);
  }

  public static List<EventuateDomainEventHandlerInfo> searchAppContextForDomainEventHandlers(ApplicationContext ctx) {
    List<EventuateDomainEventHandlerInfo> result = new ArrayList<>();
    String[] beanNames = ctx.getBeanNamesForType(Object.class);

    for (String beanName : beanNames) {
      Object bean = ctx.getBean(beanName);
      Class<?> targetClass = AopUtils.getTargetClass(bean);

      Arrays.stream(targetClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(EventuateDomainEventHandler.class))
            .map(method -> {
              return new EventuateDomainEventHandlerInfo(bean, method.getAnnotation(EventuateDomainEventHandler.class), method);
            })
            .forEach(result::add);
    }

    return result;
  }


}
