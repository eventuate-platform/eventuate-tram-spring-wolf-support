package io.eventuate.tram.spring.springwolf.sagas;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapUtils {
  static Map<String, Set<Class<?>>> combineMaps(Map<String, Set<Class<?>>> map1, Map<String, Set<Class<?>>> map2) {
    return Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (set1, set2) ->
                Stream.concat(set1.stream(), set2.stream())
                .collect(Collectors.toSet())
        ));
  }
}
