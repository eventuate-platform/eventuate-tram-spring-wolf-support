package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import io.eventuate.tram.spring.springwolf.testing.AsyncApiDocument;
import io.eventuate.tram.spring.springwolf.testing.Customer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventuateTramSpringWolfEventsTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfEventsConfiguration.class, EventConfiguration.class})
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
  private static final String CUSTOMER_EVENT_PUBLISHER = CustomerEventPublisherImpl.class.getName();
  private static final String CUSTOMER_EVENT_SUBSCRIBER = "operationId";

  private static final String ORDER_CHANNEL = "orderServiceEvents";
  private static final String ORDER_CREATED_EVENT = OrderCreatedEvent.class.getName();
  private static final String ORDER_EVENT_SUBSCRIBER = OrderEventConsumer.class.getName() + ".handleOrderCreatedEvent";

  @Test
  public void shouldExposeSpringWolf() throws IOException {
    AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc();

    assertThat(doc.getVersion())
        .as("AsyncAPI version should be 3.0.0")
        .isEqualTo("3.0.0");

    doc.assertSendsMessage(CUSTOMER_EVENT_PUBLISHER, CUSTOMER_CHANNEL, CUSTOMER_CREATED_EVENT);
    doc.assertReceivesMessage(CUSTOMER_EVENT_SUBSCRIBER, CUSTOMER_CHANNEL, CUSTOMER_CREATED_EVENT);
    doc.assertReceivesMessage(ORDER_EVENT_SUBSCRIBER, ORDER_CHANNEL, ORDER_CREATED_EVENT);
  }

}
