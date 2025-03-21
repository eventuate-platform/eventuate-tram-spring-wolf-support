package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramOperationsScanner;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelReference;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.asyncapi.v3.model.operation.OperationAction;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class OperationsFromSagasScanner implements EventuateTramOperationsScanner {

  private final List<SagaProxyInfo> sagas;

  public OperationsFromSagasScanner(List<SimpleSaga<?>> sagas) {
    this.sagas = sagas.stream().map(SagaProxyInfoFactory::make).toList();
  }

  @Override
  public ElementsWithClasses<Operation> scan() {
    Set<SendOperation> sendOperations = sendOperationsFromSagas(sagas);
    Set<ReceiveOperation> receiveOperations = receiveOperationsFromSagas(sagas);
    return new ElementsWithClasses<>(makeOperations(sendOperations, receiveOperations), makeClasses(sendOperations, receiveOperations));
  }

  private Map<String, Operation> makeOperations(Set<SendOperation> sendOperations, Set<ReceiveOperation> receiveOperations) {
    Map<String, Operation> sendMap = sendOperations.stream().collect(Collectors.toMap(SendOperation::operationName, this::makeSendOperation));
    Map<String, Operation> receiveMap = receiveOperations.stream().collect(Collectors.toMap(ReceiveOperation::operationName, this::makeReceiveOperation));
    Map<String, Operation> result = new HashMap<>(sendMap);
    result.putAll(receiveMap);
    return result;
  }

  private Operation makeSendOperation(SendOperation so) {
    String channel = so.commandChannel();
    return Operation.builder()
        .operationId(so.operationName())
        .action(OperationAction.SEND)
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .messages(List.of(MessageReference.toChannelMessage(channel, so.commandClass().getName())))
        .build();
  }
  private Operation makeReceiveOperation(ReceiveOperation ro) {
    String channel = ro.replyChannel();
    return Operation.builder()
        .operationId(ro.operationName())
        .action(OperationAction.RECEIVE)
        .channel(ChannelReference.builder()
            .ref("#/channels/" + channel)
            .build())
        .messages(ro.classes().stream().map(aClass -> MessageReference.toChannelMessage(channel, aClass.getName())).collect(Collectors.toList()))
        .build();
  }

  private MessageReference makeMessageReference(Class<?> aClass) {
    return MessageReference.toComponentMessage(aClass.getName());
  }

  private Set<Class<?>> makeClasses(Set<SendOperation> operations, Set<ReceiveOperation> receiveOperations) {
    Stream<? extends Class<?>> s1 = operations.stream().map(SendOperation::commandClass);
    Stream<Class<?>> b = receiveOperations.stream().map(ReceiveOperation::classes).flatMap(Collection::stream);
    return Stream.concat(s1,
        b).collect(Collectors.toSet());
  }

  private Set<SendOperation> sendOperationsFromSagas(List<SagaProxyInfo> sagas) {
    return sagas.stream().map(SagaProxyInfo::sendOperations).flatMap(Collection::stream).collect(Collectors.toSet());
  }
  private Set<ReceiveOperation> receiveOperationsFromSagas(List<SagaProxyInfo> sagas) {
    return sagas.stream().map(SagaProxyInfo::receiveOperations).collect(Collectors.toSet());
  }

}
