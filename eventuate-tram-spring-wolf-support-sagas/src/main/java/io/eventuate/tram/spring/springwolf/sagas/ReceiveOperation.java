package io.eventuate.tram.spring.springwolf.sagas;

public record ReceiveOperation(String sagaType, String replyChannel, java.util.Set<Class<?>> classes) {
  public String operationName() {
    return "receive-" + replyChannel;
  }
}
