package io.eventuate.exampleapp.events.consumer;

import io.eventuate.exampleapp.events.common.Customer;
import io.eventuate.exampleapp.events.common.CustomerCreatedEvent;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import io.eventuate.tram.spring.springwolf.events.EventuateSpringWolfEventsConfiguration;
import io.eventuate.tram.spring.springwolf.testing.AsyncApiDocument;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventuateTramSpringWolfEventsConsumerTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfEventsConfiguration.class, CustomerEventConsumerConfiguration.class})
  public static class Config {
  }

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  private static final String CUSTOMER_CHANNEL = Customer.class.getName();
  private static final String CUSTOMER_CREATED_EVENT = CustomerCreatedEvent.class.getName();
  private static final String CUSTOMER_EVENT_SUBSCRIBER = "io.eventuate.exampleapp.events.consumer.CustomerEventConsumer.handleCustomerCreatedEvent";

  @Test
  public void shouldExposeSpringWolf() {
    AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc("springwolf-consumer.json");

    doc.assertReceivesMessage(CUSTOMER_EVENT_SUBSCRIBER, CUSTOMER_CHANNEL, CUSTOMER_CREATED_EVENT);
  }

  @Test
  public void shouldExposeSpringWolfUi() {

    RestAssured.given()
        .get("/springwolf/asyncapi-ui.html")
        .then()
        .statusCode(200);

  }

}
