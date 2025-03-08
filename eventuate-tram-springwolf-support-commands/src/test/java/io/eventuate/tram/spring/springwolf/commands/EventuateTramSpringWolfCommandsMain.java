package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.CustomersAndOrdersConfiguration;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfCommandsConfiguration.class, CustomersAndOrdersConfiguration.class})
public class EventuateTramSpringWolfCommandsMain {

  public static void main(String[] args) {
    SpringApplication.run(EventuateTramSpringWolfCommandsMain.class, args);
  }
}
