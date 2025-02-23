package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.core.asyncapi.scanners.OperationsScanner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventuateTramOperationsScannerManager implements OperationsScanner {

  private final List<EventuateTramOperationsScanner> scanners;

  public EventuateTramOperationsScannerManager(List<EventuateTramOperationsScanner> scanners) {
    this.scanners = scanners;
  }

  @Autowired
  private SpringWolfMessageFactory springWolfMessageFactory;


  @Override
  public Map<String, Operation> scan() {
    List<OperationsWithClasses> results = scanners.stream()
        .map(EventuateTramOperationsScanner::scan).toList();

    results.stream()
        .flatMap(r -> r.classes().stream())
        .collect(Collectors.toSet())
        .forEach(springWolfMessageFactory::makeMessageFromClass);

    return results.stream()
            .map(OperationsWithClasses::operations)
            .reduce(new HashMap<>(), (result, map) -> {
              result.putAll(map);
              return result;
            });
  }


}
