package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfSagasConfiguration.class, io.eventuate.tram.spring.springwolf.sagas.application.CustomersAndOrdersConfiguration.class})
public class EventuateTramSpringWolfSagasMain {

  public static void main(String[] args) {
    SpringApplication.run(EventuateTramSpringWolfSagasMain.class, args);
  }
}
