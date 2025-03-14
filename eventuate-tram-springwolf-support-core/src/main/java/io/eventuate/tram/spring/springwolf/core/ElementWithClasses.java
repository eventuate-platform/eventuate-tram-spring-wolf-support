package io.eventuate.tram.spring.springwolf.core;

import java.util.Set;

public record ElementWithClasses<T>(T element, Set<Class<?>> classes) {
}
