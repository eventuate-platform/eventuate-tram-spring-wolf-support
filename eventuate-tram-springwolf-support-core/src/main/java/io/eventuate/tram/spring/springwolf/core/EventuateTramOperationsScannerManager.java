package io.eventuate.tram.spring.springwolf.core;

import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.core.asyncapi.scanners.OperationsScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventuateTramOperationsScannerManager implements OperationsScanner {

  private final List<EventuateTramOperationsScanner> scanners;
  private final SpringWolfMessageFactory springWolfMessageFactory;

  public EventuateTramOperationsScannerManager(List<EventuateTramOperationsScanner> scanners, SpringWolfMessageFactory springWolfMessageFactory) {
    this.scanners = scanners;
    this.springWolfMessageFactory = springWolfMessageFactory;
  }


  @Override
  public Map<String, Operation> scan() {
    List<ElementsWithClasses<Operation>> results = scanners.stream()
        .map(EventuateTramOperationsScanner::scan).toList();

    results.stream()
        .flatMap(r -> r.classes().stream())
        .collect(Collectors.toSet())
        .forEach(springWolfMessageFactory::makeMessageFromClass);

    return results.stream()
            .map(ElementsWithClasses::elements)
            .reduce(new HashMap<>(), (result, map) -> {
              result.putAll(map);
              return result;
            });
  }


}
