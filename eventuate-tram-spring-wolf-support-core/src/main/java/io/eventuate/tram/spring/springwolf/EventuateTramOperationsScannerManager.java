package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import io.github.springwolf.core.asyncapi.scanners.OperationsScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventuateTramOperationsScannerManager implements OperationsScanner {

  private final List<EventuateTramOperationsScanner> scanners;

  public EventuateTramOperationsScannerManager(List<EventuateTramOperationsScanner> scanners) {
    this.scanners = scanners;
  }

  @Override
  public Map<String, Operation> scan() {
    return scanners.stream()
            .map(EventuateTramOperationsScanner::scan)
            .reduce(new HashMap<>(), (result, map) -> {
              result.putAll(map);
              return result;
            });
  }


}
