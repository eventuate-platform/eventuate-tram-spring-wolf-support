package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.operation.Operation;

public interface EventuateTramOperationsScanner {
  ElementsWithClasses<Operation> scan();
}
