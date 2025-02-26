package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantOperation;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantProxy;
import io.eventuate.tram.spring.springwolf.MessageClassScanner;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodIntrospector;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SagaProxyInfoFactory {

  public static SagaProxyInfo make(SimpleSaga<?> saga) {
    List<SagaParticipantProxyInfo> participants = getSagaParticipants(saga);
    String replyChannel = AopUtils.getTargetClass(saga).getName() + "-reply";
    return new SagaProxyInfo(saga.getSagaType(), replyChannel, participants);
  }

  private static List<SagaParticipantProxyInfo> getSagaParticipants(SimpleSaga<?> saga) {
    // HACK!!!!
    return saga.getParticipantProxies().stream().map(SagaProxyInfoFactory::makeSagaParticipantProxyInfo).collect(Collectors.toList());
  }

  private static SagaParticipantProxyInfo makeSagaParticipantProxyInfo(Object sagaParticipant) {
    Class<?> targetClass = AopUtils.getTargetClass(sagaParticipant);
    SagaParticipantProxy sagaParticipantProxy = targetClass.getAnnotation(SagaParticipantProxy.class);
    Set<SagaParticipantOperationInfo> operations = MethodIntrospector.selectMethods(targetClass,
            (MethodIntrospector.MetadataLookup<SagaParticipantOperation>) method ->
                method.getAnnotation(SagaParticipantOperation.class)).entrySet().stream()
        .map(entry -> makeSagaParticipantOperationInfo(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
    return new SagaParticipantProxyInfo(sagaParticipantProxy.channel(), operations);
  }

  private static SagaParticipantOperationInfo makeSagaParticipantOperationInfo(Method method, SagaParticipantOperation po) {
    Class<?> commandClass = po.commandClass();
    Set<Class<?>> replyClasses = Arrays.stream(po.replyClasses()).flatMap(rc -> MessageClassScanner.findConcreteImplementorsOf(rc).stream()).collect(Collectors.toSet());
    return new SagaParticipantOperationInfo(method.getName(), commandClass, replyClasses);
  }

}
