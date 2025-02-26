package io.eventuate.tram.spring.springwolf.sagas;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.eventuate.tram.spring.springwolf.sagas.MapUtils.combineMaps;
import static org.assertj.core.api.Assertions.assertThat;


class MapUtilsTest {

  @Test
  public void shouldCombine() {
    Map<String, Set<Class<?>>> map1 = Map.of("x", Set.of(String.class, Integer.class),
        "y", Set.of(Map.class));
    Map<String, Set<Class<?>>> map2 = Map.of("x", Set.of(String.class, Double.class),
        "z", Set.of(List.class));
    Map<String, Set<Class<?>>> expectedResult = Map.of("x", Set.of(String.class, Integer.class, Double.class),
        "y", Set.of(Map.class),"z", Set.of(List.class));
    assertThat(combineMaps(map1, map2)).isEqualTo(expectedResult);
  }
}