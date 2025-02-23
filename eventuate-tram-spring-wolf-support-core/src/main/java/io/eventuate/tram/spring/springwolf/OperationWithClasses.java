package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.operation.Operation;

import java.util.Set;

public record OperationWithClasses(Operation operation, Set<Class<?>> classes) {
}
