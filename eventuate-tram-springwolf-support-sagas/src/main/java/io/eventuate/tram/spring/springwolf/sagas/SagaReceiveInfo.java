package io.eventuate.tram.spring.springwolf.sagas;

import java.util.Set;

public record SagaReceiveInfo(String replyChannel, Set<Class<?>> replyClasses) {
}
