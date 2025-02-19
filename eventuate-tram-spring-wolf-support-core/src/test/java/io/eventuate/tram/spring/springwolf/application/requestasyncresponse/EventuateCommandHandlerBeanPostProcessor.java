package io.eventuate.tram.spring.springwolf.application.requestasyncresponse;

import io.eventuate.tram.spring.springwolf.EventuateCommandHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class EventuateCommandHandlerBeanPostProcessor implements BeanPostProcessor {

  private final EventuateCommandDispatcher eventuateCommandDispatcher;

  public EventuateCommandHandlerBeanPostProcessor(EventuateCommandDispatcher eventuateCommandDispatcher) {
    this.eventuateCommandDispatcher = eventuateCommandDispatcher;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> clazz = bean.getClass();
    for (Method method : clazz.getMethods()) {
      EventuateCommandHandler eventuateCommandHandler = method.getAnnotation(EventuateCommandHandler.class);
      if (eventuateCommandHandler != null) {
        eventuateCommandDispatcher.registerHandlerMethod(bean, eventuateCommandHandler, method);
      }
    }
    return bean;
  }
}
