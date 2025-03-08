package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfEventsConfiguration.class, EventConfiguration.class})
public class EventuateTramSpringWolfEventsMain {

  public static void main(String[] args) {
    SpringApplication.run(EventuateTramSpringWolfEventsMain.class, args);
  }
}
