package io.eventuate.exampleapp.events.legacyconsumer;

import io.eventuate.exampleapp.events.common.OrderCreatedEvent;
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
public class EventuateTramSpringWolfEventsLegacyConsumerTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class,
      EventuateSpringWolfEventsConfiguration.class,
      OrderEventConsumerConfiguration.class})
  public static class Config {
  }

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  private static final String ORDER_CHANNEL = "io.eventuate.exampleapp.events.common.Order";
  private static final String ORDER_CREATED_EVENT = OrderCreatedEvent.class.getName();
  private static final String ORDER_EVENT_SUBSCRIBER = "receive-customerServiceEvents-io.eventuate.exampleapp.events.common.Order";

  @Test
  public void shouldExposeSpringWolf() {
    AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc("springwolf-legacy-consumer.json");

    doc.assertReceivesMessage(ORDER_EVENT_SUBSCRIBER, ORDER_CHANNEL, ORDER_CREATED_EVENT);
  }

  @Test
  public void shouldExposeSpringWolfUi() {

    RestAssured.given()
        .get("/springwolf/asyncapi-ui.html")
        .then()
        .statusCode(200);

  }

}
