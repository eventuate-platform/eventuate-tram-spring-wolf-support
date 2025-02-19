package io.eventuate.tram.spring.springwolf;

import java.lang.reflect.Method;

public record CommandHandlerInfo(Object bean, EventuateCommandHandler eventuateCommandHandler, Method method) {
}
