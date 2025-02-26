package io.eventuate.tram.spring.springwolf.core.events;

import io.eventuate.tram.spring.springwolf.core.MessageClassScanner;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MessageClassScannerTest {

  interface CustomerEvent {
  }

  static class CustomerCreatedEvent implements CustomerEvent {
  }

  @Test
  public void shouldFindConcreteEventImplementations() {
    Set<Class<?>> implementations = MessageClassScanner.findConcreteImplementorsOf(CustomerEvent.class);
    assertThat(implementations).isEqualTo(Set.of(CustomerCreatedEvent.class));
  }

}
