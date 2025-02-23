package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.operation.Operation;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record OperationsWithClasses(Map<String, Operation> operations, Set<Class<?>> classes)  {
  public OperationsWithClasses(Map<String, Operation> operations) {
    this(operations, Set.of());
  }

  static OperationsWithClasses make(Map<String, OperationWithClasses> operationsWithClasses) {
    return new OperationsWithClasses(operationsWithClasses.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            e -> e.getValue().operation())),
        operationsWithClasses.values().stream().flatMap(owc -> owc.classes().stream()).collect(Collectors.toSet()));
  }
}
