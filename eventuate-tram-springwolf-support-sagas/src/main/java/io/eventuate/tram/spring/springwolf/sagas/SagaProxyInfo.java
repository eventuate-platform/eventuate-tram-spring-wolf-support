package io.eventuate.tram.spring.springwolf.sagas;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record SagaProxyInfo(String sagaType, String replyChannel, List<SagaParticipantProxyInfo> participants) {
  public Map<String, Set<Class<?>>> makeReplyChannelsToClasses() {
    return Map.of(replyChannel, replyClasses());
  }

  private Set<Class<?>> replyClasses() {
    return participants
        .stream()
        .map(SagaParticipantProxyInfo::replyClasses)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  public Map<String, Set<Class<?>>> makCommandChannelsToClasses() {
    return participants.stream().map(SagaParticipantProxyInfo::makCommandChannelsToClasses).reduce(Map.of(), MapUtils::combineMaps);
  }

  public Set<SendOperation> sendOperations() {
    return participants.stream().map(sp -> sp.sendOperations(sagaType)).flatMap(Collection::stream).collect(Collectors.toSet());
  }

  public ReceiveOperation receiveOperations() {
    return new ReceiveOperation(sagaType, replyChannel, replyClasses());
  }
}
