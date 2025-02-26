package io.eventuate.tram.spring.springwolf;

import java.util.HashSet;
import java.util.Set;

public class SetUtil {

  public static <T> Set<T> add(Set<T> set, T newElement) {
    Set<T> mutableSet = new HashSet<>(set);
    mutableSet.add(newElement);
    return mutableSet;
  }

}
