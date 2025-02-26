package io.eventuate.tram.spring.springwolf.sagas;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record SagaParticipantProxyInfo(String commandChannel, Set<SagaParticipantOperationInfo> operations) {

  public Collection<Class<?>> replyClasses() {
    return operations.stream().map(SagaParticipantOperationInfo::replyClasses).flatMap(Collection::stream).collect(Collectors.toSet());
  }

  public Map<String, Set<Class<?>>> makCommandChannelsToClasses() {
    return Map.of(commandChannel, operations.stream().map(SagaParticipantOperationInfo::commandClass).collect(Collectors.toSet()));
  }

  public Set<SendOperation> sendOperations(String sagaType) {
    return operations.stream().map(o -> o.sendOperation(sagaType, commandChannel)).collect(Collectors.toSet());
  }
}
