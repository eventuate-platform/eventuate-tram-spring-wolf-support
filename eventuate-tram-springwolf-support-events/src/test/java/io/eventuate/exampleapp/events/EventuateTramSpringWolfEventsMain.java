package io.eventuate.exampleapp.events;

import io.eventuate.exampleapp.events.publisher.EventPublisherConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import io.eventuate.tram.spring.springwolf.events.EventuateSpringWolfEventsConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfEventsConfiguration.class, EventPublisherConfiguration.class})
public class EventuateTramSpringWolfEventsMain {

  public static void main(String[] args) {
    SpringApplication.run(EventuateTramSpringWolfEventsMain.class, args);
  }
}
