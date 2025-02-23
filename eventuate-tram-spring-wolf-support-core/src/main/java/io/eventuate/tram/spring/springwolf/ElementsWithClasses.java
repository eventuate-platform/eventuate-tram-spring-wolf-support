package io.eventuate.tram.spring.springwolf;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record ElementsWithClasses<T>(Map<String, T> elements, Set<Class<?>> classes)  {
  public ElementsWithClasses(Map<String, T> operations) {
    this(operations, Set.of());
  }

  static <T> ElementsWithClasses<T> make(Map<String, ElementWithClasses<T>> operationsWithClasses) {
    return new ElementsWithClasses<>(operationsWithClasses.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            e -> e.getValue().element())),
        operationsWithClasses.values().stream().flatMap(owc -> owc.classes().stream()).collect(Collectors.toSet()));
  }
}
