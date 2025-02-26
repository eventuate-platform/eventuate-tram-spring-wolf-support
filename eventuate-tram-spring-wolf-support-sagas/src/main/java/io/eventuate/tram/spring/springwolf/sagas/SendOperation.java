package io.eventuate.tram.spring.springwolf.sagas;

public record SendOperation(String sagaType, String commandChannel, String name, Class<?> commandClass) {
  public String operationName() {
    return sagaType + "-" + commandChannel + "-" + name;
  }
}
