package io.eventuate.tram.spring.springwolf.sagas;

public record SagaParticipantOperationInfo(String name, Class<?> commandClass, java.util.Set<Class<?>> replyClasses) {
  public SendOperation sendOperation(String sagaType, String commandChannel) {
    return new SendOperation(sagaType, commandChannel, name, commandClass);
  }
}
