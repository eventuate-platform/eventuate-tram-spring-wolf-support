package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.spring.springwolf.application.events.CustomerCreatedEvent;
import io.eventuate.tram.spring.springwolf.application.events.CustomerEvent;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MessageClassScannerTest {

  @Test
  public void shouldFindConcreteEventImplementations() {
    Set<Class<?>> implementations = MessageClassScanner.findConcreteImplementorsOf(CustomerEvent.class);
    assertThat(implementations).isEqualTo(Set.of(CustomerCreatedEvent.class));
  }

}
